import sys
import os
import dlib
import glob
import cv2
import numpy as np

SIZE = (512,512)
detector = dlib.get_frontal_face_detector()

def segment_faces(img_folder, predictor_path, export_folder, detect_face, dir_ix):
    # TODO: This will not work if files are in different folders
    files = []
    files += (glob.glob(img_folder + "\\*.jpg"))
    files += (glob.glob(img_folder + "\\*.png"))

    if len(files) < 1:
        return

    predictor = dlib.shape_predictor(predictor_path)

    for f in files:
        img = cv2.imread(f)
        img = cv2.resize(img, (SIZE[0], SIZE[1]), interpolation=cv2.INTER_AREA)

        model_name = os.path.basename(f)[:-4]

        # Ask the detector to find the bounding boxes of each face. The 1 in the
        # second argument indicates that we should upsample the image 1 time. This
        # will make everything bigger and allow us to detect more faces.
        if detect_face:
            dets = detector(img, 1)
        else:
            dets = [dlib.rectangle(left = 0, top = 0, right = SIZE[0], bottom=SIZE[1])]

        save_path = export_folder

        for k, d in enumerate(dets):
            # Get the landmarks/parts for the face in box d.
            shape = predictor(img, d)

            # Nose
            width = shape.part(35).x - shape.part(31).x
            x = int(shape.part(31).x - (width * 0.40))
            width = int(width * 2.0)
            y = int(shape.part(27).y * 0.8)
            height = int((shape.part(33).y - y) * 1.1)


            cv2.imwrite(save_path + "nose_" + dir_ix + "\\" + model_name + ".png", cv2.resize(img[y : y+height, x:x+width], (150,250)))
            np.savetxt(save_path + "nose_" + dir_ix + "\\" + model_name + ".txt", np.array([x / SIZE[0], y / SIZE[0], width / SIZE[0], height / SIZE[1]]))

            # Right eye
            width = shape.part(39).x - shape.part(36).x
            x = int(shape.part(36).x - (width * 0.40))
            width = int(width * 2.0)
            height = int(width * (2.0 / 3.0))
            y = int(shape.part(36).y - height * 0.5)

            cv2.imwrite(save_path + "right_eye_" + dir_ix + "\\" + model_name + ".png", img[y : y+height, x:x+width])
            np.savetxt(save_path + "right_eye_" + dir_ix + "\\" + model_name + ".txt", np.array([x / SIZE[0], y / SIZE[0], width / SIZE[0], height / SIZE[1]]))

            # # Left eye
            width = shape.part(45).x - shape.part(42).x
            x = int(shape.part(42).x - (width * 0.40))
            width = int(width * 2.0)
            height = int(width * (2.0 / 3.0))
            y = int(shape.part(42).y - height * 0.5)

            cv2.imwrite(save_path + "left_eye_" + dir_ix + "\\" + model_name + ".png", img[y : y+height, x:x+width])
            np.savetxt(save_path + "left_eye_" + dir_ix + "\\" + model_name + ".txt", np.array([x / SIZE[0], y / SIZE[0], width / SIZE[0], height / SIZE[1]]))

            # # Mouth
            width = shape.part(54).x - shape.part(48).x
            x = int(shape.part(48).x - (width * 0.2))
            width = int(width * 1.5)
            height = int(width * (2.0 / 3.0))
            y = int(shape.part(48).y - height * 0.5)

            cv2.imwrite(save_path + "mouth_" + dir_ix + "\\" + model_name + ".png", img[y : y+height, x:x+width])
            np.savetxt(save_path + "mouth_" + dir_ix + "\\" + model_name + ".txt", np.array([x / SIZE[0], y / SIZE[0], width / SIZE[0], height / SIZE[1]]))


if __name__ == '__main__':
    if len(sys.argv) >= 3:
        segment_faces(sys.argv[1], sys.argv[2], sys.argv[3])
