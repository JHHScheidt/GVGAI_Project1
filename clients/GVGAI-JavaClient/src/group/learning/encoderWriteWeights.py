import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import norm
import os
import math
import sys
from pathlib import Path

from keras.models import Sequential, Model, model_from_json
from keras.layers import Input, Dense, Lambda, Layer
from keras import backend as K
from keras import metrics
from keras.datasets import mnist

# Arguments to be given:
# 1: The model specifications (numbers after model- or weights-)
# 2: The file from which predictions must be made
# Example input: "10_48s_38s_28s_18s_28s_38s_47_mean_squared_error_adam"
modelSpecs = sys.argv[1]  # if defined -> train

# Define paths and data
MODELPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "model"))
WEIGHTSPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "weights"))
RESULTSPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "results"))
modelName = "model_"+modelSpecs+".json"
weightsName = "weights_"+modelSpecs+".h5"
resultsName = "weights_"+modelSpecs+".txt"

# Read or create the model
if Path(os.path.join(MODELPATH, modelName)).exists():
    json_file = open(os.path.join(MODELPATH, modelName), 'r')
    loaded_model_json = json_file.read()
    json_file.close()
    model = model_from_json(loaded_model_json)
else:
    print("The given model does not exist")
    quit()

# Set weights if already existing
if Path(os.path.join(WEIGHTSPATH, weightsName)).exists():
    model.load_weights(os.path.join(WEIGHTSPATH, weightsName))
else:
    print("The given weights do not exist")
    quit()

np.set_printoptions(precision=16, linewidth=1000)
weightsFile = open(os.path.join(RESULTSPATH, resultsName), "w")
for i in range(0, len(model.layers)):
    totalWeights = np.insert(model.layers[i].get_weights()[0], 0, model.layers[i].get_weights()[1], axis=0)
    for j in range(0, len(totalWeights)):
        weightsFile.write(str(totalWeights[j].tolist())+"\n")
    weightsFile.write("\n")
