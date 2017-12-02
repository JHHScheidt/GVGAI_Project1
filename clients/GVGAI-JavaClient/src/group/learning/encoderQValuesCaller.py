import numpy as np
import matplotlib.pyplot as plt
from scipy.stats import norm
import os
import math
import sys
from pathlib import Path
# from QValues import run, save

from keras.models import Sequential, Model, model_from_json
from keras.layers import Input, Dense, Lambda, Layer
from keras import backend as K
from keras import metrics
from keras.datasets import mnist

# Arguments to be given:
# 1: The model specifications (numbers after model- or weights-)
# 2: The file from which predictions must be made
# Example input: "10_48s_38s_28s_18s_28s_38s_47_mean_squared_error_adam" "0_0.txt"
modelSpecs = sys.argv[1]  # if defined -> train
predictionData = sys.argv[2]

# Define paths and data
MODELPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "model"))
WEIGHTSPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "weights"))
RESULTSPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "activationValues"))
modelName = "model_"+modelSpecs+".json"
weightsName = "weights_"+modelSpecs+".h5"
resultsName = "actVals_"+modelSpecs+".txt"
DATAPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), os.pardir, os.pardir, "res", "data", "preprocessed", predictionData))
if os.path.exists(DATAPATH) and predictionData != "":
    predict = True
else:
    predict = False

batch_size = 1
epsilon_std = 1.0
printPrediction = 1

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

if predict:
    text_file = open(DATAPATH, "r")
    modelValues = modelSpecs.split("_")[0:2]
    for i in range(0, len(modelValues)):
        modelValues[i] = modelValues[i].replace("s", "")
    lines = text_file.readlines()
    # Create the training data
    # Empty data object
    input_dim = int(modelValues[1])
    output_dim = input_dim - 1
    inputData = np.zeros((math.floor(len(lines) / (int(modelValues[0]) + 3)), input_dim), dtype=np.float64)
    outputData = np.zeros((math.floor(len(lines) / (int(modelValues[0]) + 3)) - 1, output_dim), dtype=np.float64)

    # Remove unnecessary '\n'
    for i in range(0, len(lines)):
        if lines[i].endswith("\n"):
            lines[i] = lines[i][:-1]

    # Fill in input and output
    # 0: gameScore
    # 1: avatarSpeed
    # 2: avatarHealthPoints
    # 3: currentAvatarOrientation-width
    # 4: currentAvatarOrientation-height
    # 5: currentAvatarPosition-width
    # 6: currentAvatarPosition-height
    # 7+4*i: observation category
    # 8+4*i: observation x-coordinate
    # 9+4*i: observation y-coordinate
    # 10+4*i: Euclidian distance to avatar
    # 10+4*max(i)+1: action (input only)
    for i in range(0, len(lines), int(modelValues[0]) + 3):
        iteration = int((i / (int(modelValues[0]) + 3)))
        objects = lines[i + 1].split(";")
        inputData[iteration][len(inputData[iteration]) - 1] = objects[len(objects) - 1]
        objects = objects[0:len(objects) - 1]
        inputData[iteration][0:len(objects)] = objects[0:len(objects)]
        for j in range(0, int(modelValues[0])):
            obsObjects = lines[i + 2 + j].split(";")
            inputData[iteration][(len(objects) + (j * 4) + 0):(len(objects) + (j * 4) + 4)] = obsObjects[0:4]
        if iteration > 0:
            outputData[iteration - 1][0:(len(objects) + int(modelValues[0]) * 4)] = inputData[iteration][
                                                                             0:(len(objects) + int(modelValues[0]) * 4)]
    inputData = inputData[0:len(inputData) - 1]

    halfModel = Sequential()
    for i in range(1, math.ceil((len(model.layers)+1)/2)):
        if i == 1:
            halfModel.add(Dense(model.layers[i].input_shape[1],
                                input_dim=model.layers[0].input_shape[1],
                                activation=model.layers[i-1].activation,
                                weights=model.layers[i-1].get_weights()))
        else:
            halfModel.add(Dense(model.layers[i].input_shape[1],
                                activation=model.layers[i-1].activation,
                                weights=model.layers[i-1].get_weights()))

    predictions = halfModel.predict(inputData, batch_size=batch_size, verbose=printPrediction)
    print("send data:")
    print("State:")
    print(predictions[0])
    print("Reward:")
    print(inputData[0][0])
    # for i in range(0, len(predictions)):
    #     run(predictions[i], inputData[i][0])
    # save()
else:
    print("The data from which predictions must be made does not exist")