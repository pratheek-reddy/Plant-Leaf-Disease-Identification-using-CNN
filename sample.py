import numpy as np
import matplotlib.pyplot as plt
import os
dataset_path=os.path.join(os.getcwd(),'dataset')
train_path=os.path.join(dataset_path,'train')
test_path=os.path.join(dataset_path,'test')
print(train_path)
print(test_path)
print("No of labels is ",len(os.listdir(train_path)))
print("no of data points in each label is ",len(os.listdir(os.path.join(train_path,'0'))))
from matplotlib import image as imgplt
img_array=os.listdir(os.path.join(train_path,'0'))
for i in range(0,5):
    img=imgplt.imread(os.path.join(os.path.join(train_path,'0'),img_array[i]))
    plt.imshow(img)
    plt.show()