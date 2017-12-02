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
# 1: the text file from which data is read
# 2: The number of observations used after deictic view
# 3: Number of epochs
# Example input: "0_0.txt" 10 1000
data = sys.argv[1]
originalVars = 8
varsPerObs = 4
observations = int(sys.argv[2])
epochs = int(sys.argv[3])

# Define paths and data
MODELPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "model"))
WEIGHTSPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "weights"))
RESULTSPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "results"))

# Define model specs
dimensions = [
    originalVars + (varsPerObs * observations),
    8+observations*3,
    8+observations*2,
    8+observations,
    8+observations*2,
    8+observations*3,
    originalVars + (varsPerObs * observations)-1
]

activations = ["sigmoid", "sigmoid", "sigmoid", "sigmoid", "sigmoid", "sigmoid"]
loss = "mean_squared_error"
optimizer = "adam"

modelRepresentation = "_"+str(observations)+"_"
for i in range(0, len(dimensions)-1):
    modelRepresentation = modelRepresentation+str(dimensions[i])+activations[i][0]+"_"
modelRepresentation = modelRepresentation+str(dimensions[len(dimensions)-1])+"_"+loss+"_"+optimizer
modelName = "model"+modelRepresentation+".json"
weightsName = "weights"+modelRepresentation+".h5"
resultsName = "weights"+modelRepresentation+".txt"
DATAPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), os.pardir, os.pardir, "res", "data", "preprocessed", data))

train = False
if os.path.exists(DATAPATH) and data != "":
    train = True

batch_size = 1
printTraining = 1

# Read or create the model
if Path(os.path.join(MODELPATH, modelName)).exists():
    json_file = open(os.path.join(MODELPATH, modelName), 'r')
    loaded_model_json = json_file.read()
    json_file.close()
    model = model_from_json(loaded_model_json)
else:
    model = Sequential()
    for i in range(1, len(dimensions)):
        if i == 1:
            model.add(Dense(dimensions[i], input_dim=dimensions[0], activation=activations[i-1]))
        else:
            model.add(Dense(dimensions[i], activation=activations[i-1]))

    model.compile(loss=loss, optimizer=optimizer, metrics=["accuracy"])

    model_json = model.to_json()
    with open(os.path.join(MODELPATH, modelName), "w") as json_file:
        json_file.write(model_json)

# Set weights if already existing
if Path(os.path.join(WEIGHTSPATH, weightsName)).exists():
    model.load_weights(os.path.join(WEIGHTSPATH, weightsName))

if train:
    model.compile(loss=loss, optimizer=optimizer, metrics=["accuracy"])
    text_file = open(DATAPATH, "r")
    lines = text_file.readlines()
    # Create the training data
    # Empty data object
    input_dim = originalVars + (varsPerObs * observations)
    output_dim = input_dim - 1
    inputData = np.zeros((math.floor(len(lines)/(observations+3)), input_dim), dtype=np.float64)
    outputData = np.zeros((math.floor(len(lines)/(observations+3))-1, output_dim), dtype=np.float64)

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
    for i in range(0, len(lines), observations+3):
        iteration = int((i / (observations + 3)))
        objects = lines[i+1].split(";")
        inputData[iteration][len(inputData[iteration])-1] = objects[len(objects)-1]
        objects = objects[0:len(objects)-1]
        inputData[iteration][0:len(objects)] = objects[0:len(objects)]
        for j in range(0, observations):
            obsObjects = lines[i+2+j].split(";")
            inputData[iteration][(len(objects)+(j*4)+0):(len(objects)+(j*4)+4)] = obsObjects[0:4]
        if iteration > 0:
            outputData[iteration-1][0:(len(objects)+observations*4)] = \
                inputData[iteration][0:(len(objects)+observations*4)]
    inputData = inputData[0:len(inputData)-1]

    model.fit(inputData, outputData, epochs=epochs, batch_size=batch_size, verbose=printTraining)

    model.save_weights(os.path.join(WEIGHTSPATH, weightsName))
else:
    print("The given data does not exist in '\\res\\data\\preprocessed\\...'")
