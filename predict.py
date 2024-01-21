import tensorflow as tf
import numpy as np  
from tensorflow.keras.preprocessing import image
model=tf.keras.models.load_model('model.h5')
img = image.load_img('pepper_bell_healthy.jpg', target_size=(256,256))
x = image.img_to_array(img)
x = np.expand_dims(x, axis=0)
classes = model.predict(x)
print(classes)