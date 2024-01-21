import os
import random
folders = os.listdir('PlantVillage')
split_ratio= 0.9
os.mkdir('data')
os.mkdir('data/train')
os.mkdir('data/test')
for i in folders:
	os.mkdir('data/train/'+i)
	os.mkdir('data/test/'+i)
	files = os.listdir('PlantVillage/'+i)
	print(len(files))
	for j in files:
		if random.random() >= split_ratio:
			os.rename('PlantVillage/'+i+'/'+j,'data/test/'+i+'/'+j)
		else:
			os.rename('PlantVillage/'+i+'/'+j,'data/train/'+i+'/'+j)
