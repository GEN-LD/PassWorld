package scut.bbt.passworld.demo;

import android.util.Log;

import com.arcsoft.facerecognition.AFR_FSDKEngine;
import com.arcsoft.facerecognition.AFR_FSDKError;
import com.arcsoft.facerecognition.AFR_FSDKFace;
import com.arcsoft.facerecognition.AFR_FSDKVersion;
import com.guo.android_extend.java.ExtInputStream;
import com.guo.android_extend.java.ExtOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class FaceDB {
	private final String TAG = this.getClass().toString();

	public static String appid = "Bid1nGcSujUj7QBgD4Tkvb8VVVuGYThBCA7EoBW65aA9";
	public static String ft_key = "7tRkVnmLW39zewv4Jud5uwUYV1usHy6UWdvpVhDy86Ao";
	public static String fd_key = "7tRkVnmLW39zewv4Jud5uwUfeRB5T1Y3y4xt6swoATjT";
	public static String fr_key = "7tRkVnmLW39zewv4Jud5uwUnopSGG1bhrajLBtAQUKaD";

	public String mDBPath;
	public List<FaceRegist> mRegister;
	public AFR_FSDKEngine mFREngine;
	public AFR_FSDKVersion mFRVersion;
	public boolean mUpgrade;

	public class FaceRegist {
		public String mName;
		public List<AFR_FSDKFace> mFaceList;

		public FaceRegist(String name) {
			mName = name;
			mFaceList = new ArrayList<>();
		}
	}


	public FaceDB(String path) {
		mDBPath = path;
		mRegister = new ArrayList<>();
		mFRVersion = new AFR_FSDKVersion();
		mUpgrade = false;
		mFREngine = new AFR_FSDKEngine();
		AFR_FSDKError error = mFREngine.AFR_FSDK_InitialEngine(FaceDB.appid, FaceDB.fr_key);
		if (error.getCode() != AFR_FSDKError.MOK) {
			Log.e(TAG, "AFR_FSDK_InitialEngine fail! error code :" + error.getCode());
		} else {
			mFREngine.AFR_FSDK_GetVersion(mFRVersion);
			Log.d(TAG, "AFR_FSDK_GetVersion=" + mFRVersion.toString());
		}
	}

	public void destroy() {
		if (mFREngine != null) {
			mFREngine.AFR_FSDK_UninitialEngine();
		}
	}

	private boolean saveInfo() {
		try {
			FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt");
			ExtOutputStream bos = new ExtOutputStream(fs);
			bos.writeString(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel());
			bos.close();
			fs.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean loadInfo() {
		try {
			FileInputStream fs = new FileInputStream(mDBPath + "/face.txt");
			ExtInputStream bos = new ExtInputStream(fs);
			//load version
			String version_saved = bos.readString();
			if (version_saved.equals(mFRVersion.toString() + "," + mFRVersion.getFeatureLevel())) {
				mUpgrade = true;
			}
			//load all regist name.
			if (version_saved != null) {
				for (String name = bos.readString(); name != null; name = bos.readString()){
					mRegister.add(new FaceRegist(new String(name)));
				}
			}
			bos.close();
			fs.close();
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean loadFaces(){
		if (loadInfo()) {
			try {
				for (FaceRegist face : mRegister) {
					Log.d(TAG, "load name:" + face.mName + "'s face feature data.");
					FileInputStream fs = new FileInputStream(mDBPath + "/" + face.mName + ".data");
					ExtInputStream bos = new ExtInputStream(fs);
					AFR_FSDKFace afr = null;
					do {
						if (afr != null) {
							if (mUpgrade) {
								//upgrade data.
							}
							face.mFaceList.add(afr);
						}
						afr = new AFR_FSDKFace();
					} while (bos.readBytes(afr.getFeatureData()));
					bos.close();
					fs.close();
				}
				return true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			if (!saveInfo()) {
				Log.e(TAG, "save fail!");
			}
		}
		return false;
	}

	public	void addFace(String name, AFR_FSDKFace face) {
		try {
			//check if already registered.
			boolean add = true;
			for (FaceRegist frface : mRegister) {
				if (frface.mName.equals(name)) {
					frface.mFaceList.add(face);
					add = false;
					break;
				}
			}
			if (add) { // not registered.
				FaceRegist frface = new FaceRegist(name);
				frface.mFaceList.add(face);
				mRegister.add(frface);
			}

			if (!new File(mDBPath + "/face.txt").exists()) {
				if (!saveInfo()) {
					Log.e(TAG, "save fail!");
				}
			}

			//save name
			FileOutputStream fs = new FileOutputStream(mDBPath + "/face.txt", true);
			ExtOutputStream bos = new ExtOutputStream(fs);
			bos.writeString(name);
			bos.close();
			fs.close();

			//save feature
			fs = new FileOutputStream(mDBPath + "/" + name + ".data", true);
			bos = new ExtOutputStream(fs);
			bos.writeBytes(face.getFeatureData());
			bos.close();
			fs.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean upgrade() {
		return false;
	}

}
