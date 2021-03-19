import os
os.environ["TF_CPP_MIN_LOG_LEVEL"] = "3"  # Remove debug msgs
import sys
import src.segmentation as sg
import src.detect_landmarks as dl

EXPORT_FOLDER = ".\\tmp\\"
sys.path.append(".\\src")

def main():
    if len(sys.argv) < 5:
        print(sys.argv)
        return

    # Create tmp folders
    if not os.path.exists(EXPORT_FOLDER):
        os.makedirs(EXPORT_FOLDER)
    if not os.path.exists(EXPORT_FOLDER + "nose_" + sys.argv[4]):
        os.makedirs(EXPORT_FOLDER + "nose_" + sys.argv[4])
    if not os.path.exists(EXPORT_FOLDER + "mouth_" + sys.argv[4]):
        os.makedirs(EXPORT_FOLDER + "mouth_" + sys.argv[4])
    if not os.path.exists(EXPORT_FOLDER + "right_eye_" + sys.argv[4]):
        os.makedirs(EXPORT_FOLDER + "right_eye_" + sys.argv[4])
    if not os.path.exists(EXPORT_FOLDER + "left_eye_" + sys.argv[4]):
        os.makedirs(EXPORT_FOLDER + "left_eye_" + sys.argv[4])

    sg.segment_faces(sys.argv[1], sys.argv[2], EXPORT_FOLDER, sys.argv[3], sys.argv[4])

    dl.find_landmarks(EXPORT_FOLDER + "mouth_" + sys.argv[4], ".\\models\\mouth_128_8lmks.h5", EXPORT_FOLDER + "res_mouth_" + sys.argv[4] + ".txt", ".\\models\\normalization\\mean_mouth.npy", ".\\models\\normalization\\std_mouth.npy", 8)
    dl.find_landmarks(EXPORT_FOLDER + "right_eye_" + sys.argv[4], ".\\models\\rEye_128_4lmks.h5", EXPORT_FOLDER + "res_rEye_" + sys.argv[4] + ".txt", ".\\models\\normalization\\mean_rEye.npy", ".\\models\\normalization\\mean_rEye.npy", 4)
    dl.find_landmarks(EXPORT_FOLDER + "left_eye_" + sys.argv[4], ".\\models\\lEye_128_4lmks.h5", EXPORT_FOLDER + "res_lEye_" + sys.argv[4] + ".txt", ".\\models\\normalization\\mean_lEye.npy",".\\models\\normalization\\mean_lEye.npy", 4)
    dl.find_landmarks(EXPORT_FOLDER + "nose_" + sys.argv[4], ".\\models\\nose_128_5lmks.h5", EXPORT_FOLDER + "res_nose_" + sys.argv[4] + ".txt", ".\\models\\normalization\\mean_nose.npy",".\\models\\normalization\\mean_nose.npy", 5)

if __name__ == '__main__':
    main()
