import os
import sys
# FORCE CPU USE (memory issues)
# os.environ["CUDA_DEVICE_ORDER"] = "PCI_BUS_ID"   # see issue #152
# os.environ["CUDA_VISIBLE_DEVICES"] = "-1"
os.environ["TF_CPP_MIN_LOG_LEVEL"] = "3"  # Remove debug msgs

import matplotlib.pyplot as plt
import matplotlib.image as mpimg
import tensorflow as tf
import keras, sys, time, os, warnings, cv2
from skimage import transform
from skimage.transform import SimilarityTransform, AffineTransform

from keras.models import *
from keras.layers import *
from keras.preprocessing.image import load_img, img_to_array
from keras.utils import plot_model

import numpy as np
import pandas as pd
import glob
import random
from skimage.transform import resize

# CONFIG (uncomment if want to use GPU)
warnings.filterwarnings("ignore")

os.environ["CUDA_DEVICE_ORDER"] = "PCI_BUS_ID"
config = tf.compat.v1.ConfigProto()
config.gpu_options.per_process_gpu_memory_fraction = 0.95
config.gpu_options.visible_device_list = "0"
tf.compat.v1.keras.backend.set_session(tf.compat.v1.Session(config=config))

def gaussian_k(x0,y0,sigma,height, width):
        """ Make a square gaussian kernel centered at (x0, y0) with sigma as SD.
        """
        x = np.arange(0, width, 1, float) ## (width,)
        y = np.arange(0, height, 1, float)[:, np.newaxis] ## (height,1)
        return np.exp(-((x-x0)**2 + (y-y0)**2) / (2*sigma**2))

def generate_hm(height, width ,landmarks,s=3):
        """ Generate a full Heap Map for every landmarks in an array
        Args:
            height    : The height of Heat Map (the height of target output)
            width     : The width  of Heat Map (the width of target output)
            joints    : [(x1,y1),(x2,y2)...] containing landmarks
            maxlenght : Lenght of the Bounding Box
        """
        Nlandmarks = len(landmarks)
        hm = np.zeros((height, width, Nlandmarks), dtype = np.float32)
        for i in range(Nlandmarks):
            if not np.array_equal(landmarks[i], [-1,-1]):

                hm[:,:,i] = gaussian_k(landmarks[i][0],
                                        landmarks[i][1],
                                        s,height, width)
            else:
                hm[:,:,i] = np.zeros((height,width))
        return hm

def get_y_as_heatmap(df,height,width, sigma):

    columns_lmxy = df.columns[:-1] ## the last column contains Image
    columns_lm = []
    for c in columns_lmxy:
        c = c[:-1]
        if c not in columns_lm:
            columns_lm.extend([c])

    y_train = []
    for i in range(df.shape[0]):
        landmarks = []
        for colnm in columns_lm:
            x = df[colnm + "x"].iloc[i] * width
            y = df[colnm + "y"].iloc[i] * height
            if np.isnan(x) or np.isnan(y):
                x, y = -1, -1
            landmarks.append([x,y])

        y_train.append(generate_hm(height, width, landmarks, sigma))
    y_train = np.array(y_train)


    return(y_train,df[columns_lmxy],columns_lmxy)

def load(img_path,width=96,height=96,sigma=5):
    """
    load test/train data
    cols : a list containing landmark label names.
           If this is specified, only the subset of the landmark labels are
           extracted. for example, cols could be:

          [left_eye_center_x, left_eye_center_y]

    return:
    X:  2-d numpy array (Nsample, Ncol*Nrow)
    y:  2-d numpy array (Nsample, Nlandmarks*2)
        In total there are 15 landmarks.
        As x and y coordinates are recorded, u.shape = (Nsample,30)
    y0: panda dataframe containins the landmarks

    """
    from sklearn.utils import shuffle

    im = cv2.imread(img_path)
    im = cv2.cvtColor(im, cv2.COLOR_BGR2GRAY)
    final_data = np.array([im], dtype='float32')
    df = pd.DataFrame()

    df['Image'] = [final_data]
    df = df.fillna(-1)

    X = np.vstack(df['Image'].values) / 255.  # changes valeus between 0 and 1
    X = X.astype(np.float32)

    y, y0, nm_landmark = None, None, None

    return X, nm_landmark

def load2d(img_path,width=96,height=96,sigma=5):

    re   = load(img_path,width,height,sigma)
    # RESIZE only when memory is not a concern
    X = resize(re[0], (1,height, width))
    nm_landmarks = re[1]

    return X, nm_landmarks

def get_ave_xy(hmi, n_points = 4, thresh=0):
    '''
    hmi      : heatmap np array of size (height,width)
    n_points : x,y coordinates corresponding to the top  densities to calculate average (x,y) coordinates


    convert heatmap to (x,y) coordinate
    x,y coordinates corresponding to the top  densities
    are used to calculate weighted average of (x,y) coordinates
    the weights are used using heatmap

    if the heatmap does not contain the probability >
    then we assume there is no predicted landmark, and
    x = -1 and y = -1 are recorded as predicted landmark.
    '''
    if n_points < 1:
        ## Use all
        hsum, n_points = np.sum(hmi), len(hmi.flatten())
        ind_hmi = np.array([range(input_width)]*input_height)
        i1 = np.sum(ind_hmi * hmi)/hsum
        ind_hmi = np.array([range(input_height)]*input_width).T
        i0 = np.sum(ind_hmi * hmi)/hsum
    else:
        ind = hmi.argsort(axis=None)[-n_points:] ## pick the largest n_points
        topind = np.unravel_index(ind, hmi.shape)
        index = np.unravel_index(hmi.argmax(), hmi.shape)
        i0, i1, hsum = 0, 0, 0
        for ind in zip(topind[0],topind[1]):
            h  = hmi[ind[0],ind[1]]
            hsum += h
            i0   += ind[0]*h
            i1   += ind[1]*h

        i0 /= hsum
        i1 /= hsum
    if hsum/n_points <= thresh:
        i0, i1 = -1, -1
    return([i1,i0])

def transfer_xy_coord(hm, n_points = 64, thresh=0.2):
    '''
    hm : np.array of shape (height,width, n-heatmap)

    transfer heatmap to (x,y) coordinates

    the output contains np.array (Nlandmark * 2,)
    * 2 for x and y coordinates, containing the landmark location.
    '''
    assert len(hm.shape) == 3
    Nlandmark = hm.shape[-1]
    #est_xy = -1*np.ones(shape = (Nlandmark, 2))
    est_xy = []
    for i in range(Nlandmark):
        hmi = hm[:,:,i]
        est_xy.extend(get_ave_xy(hmi, n_points, thresh))
    return(est_xy) ## (Nlandmark * 2,)

def transfer_target(y_pred, thresh=0, n_points = 64):
    '''
    y_pred : np.array of the shape (N, height, width, Nlandmark)

    output : (N, Nlandmark * 2)
    '''
    y_pred_xy = []
    for i in range(y_pred.shape[0]):
        hm = y_pred[i]
        y_pred_xy.append(transfer_xy_coord(hm,n_points, thresh))
    return(np.array(y_pred_xy))

def find_weight(y_tra):
    '''
    :::input:::

    y_tra : np.array of shape (N_image, height, width, N_landmark)

    :::output:::

    weights :
        np.array of shape (N_image, height, width, N_landmark)
        weights[i_image, :, :, i_landmark] = 1
                        if the (x,y) coordinate of the landmark for this image is recorded.
        else  weights[i_image, :, :, i_landmark] = 0

    '''
    weight = np.zeros_like(y_tra)
    count0, count1 = 0, 0
    for irow in range(y_tra.shape[0]):
        for ifeat in range(y_tra.shape[-1]):
            if np.all(y_tra[irow,:,:,ifeat] == 0):
                value = 0
                count0 += 1
            else:
                value = 1
                count1 += 1
            weight[irow,:,:,ifeat] = value
    return(weight)


def flatten_except_1dim(weight,ndim=2):
    '''
    change the dimension from:
    (a,b,c,d,..) to (a, b*c*d*..) if ndim = 2
    (a,b,c,d,..) to (a, b*c*d*..,1) if ndim = 3
    '''
    n = weight.shape[0]
    if ndim == 2:
        shape = (n,-1)
    elif ndim == 3:
        shape = (n,-1,1)
    else:
        print("Not implemented!")
    weight = weight.reshape(*shape)
    return(weight)

def normalize_data(data, mean, std):
    return (data - mean) / std

def reshape_saved_face(face_path):
    face = np.load(face_path)
    img = cv2.resize(face, SIZE, interpolation = cv2.INTER_CUBIC)
    return np.array([img], dtype='float32').reshape(SIZE[0], SIZE[1], 1)


batch_size = 32
const = 10

# Has to be divided by 4
SIZE = (128,128)
RESIZE = True
BATCH_SIZE = 32

def find_landmarks(train_imgs, prediction_model, export_path, mean_face_path, std_face_path, num_lmks):
    X_test = []
    # TODO: need mean and std for facial parts
    mean_face = reshape_saved_face(mean_face_path)
    std_face = reshape_saved_face(std_face_path)
    train_images = glob.glob(train_imgs + "\\*.png")

    for i in train_images:
        X, nm_landmarks = load2d(i, width=SIZE[0], height=SIZE[1], sigma=5)
        X_test.append(X)

    if len(train_images) == 0:
        return

    X_test = np.array(X_test)
    X_test = X_test[:,0,:,:]
    X_test = X_test.reshape((X_test.shape[0], X_test.shape[1], X_test.shape[2], 1))

    X_test = normalize_data(X_test, mean_face, std_face)

    input_height, input_width = SIZE[1], SIZE[0]
    ## output shape is the same as input
    output_height, output_width = input_height, input_width
    n = 32*5
    nClasses = num_lmks

    model = keras.models.load_model(prediction_model)

    y_pred_test = model.predict(X_test)  ## estimated heatmap
    y_pred_test = y_pred_test.reshape(-1,output_height,output_width,nClasses)
    y_pred_test_xy = transfer_target(y_pred_test,thresh=0,n_points=11)

    for res in y_pred_test_xy:
        for i in range(0, nClasses * 2, 2):
            res[i] /= SIZE[0]
            res[i+1] /= SIZE[1]

    np.savetxt(export_path, y_pred_test_xy)

if __name__ == '__main__':
    if len(sys.argv) >= 2:
        pass
        #find_landmarks(sys.argv[1], sys.argv[2], "..\\tmp\\res.txt")
