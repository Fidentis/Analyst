import dlib
import os
import glob
from skimage import io
from scipy import misc
import numpy as np
import sys
from PIL import Image


def main():
    if len(sys.argv) < 2:
        return

    image_size = (256, 256)
    face_image = sys.argv[2]
    predictor_path = sys.argv[1]

    detector = dlib.get_frontal_face_detector()

    predictor = dlib.shape_predictor(predictor_path)

    img = Image.open(face_image)
    img = img.resize(image_size)
    img = np.array(img)


    # Ask the detector to find the bounding boxes of each face. The 1 in the
    # second argument indicates that we should upsample the image 1 time. This
    # will make everything bigger and allow us to detect more faces.
    dets = detector(img, 1)

    save_path = os.path.join(os.path.dirname(os.path.realpath(__file__)),os.path.basename(face_image) + '.txt')

    for k, d in enumerate(dets):

        # Get the landmarks/parts for the face in box d.
        shape = predictor(img, d)


        # Just kinda assuming there's 68 of them
        vec = np.empty([68, 2], dtype = float)
        for b in range(68):
            vec[b][0] = shape.part(b).x / image_size[0]
            vec[b][1] = 1 - shape.part(b).y / image_size[1]

        np.savetxt(save_path, vec)


if __name__ == '__main__':
    main()
