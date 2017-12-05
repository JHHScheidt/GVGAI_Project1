import os
import math
import sys
# from QValues import run, save
from encoderGeneralFunctions import readJsonData, loadModel, loadModelWeights

from keras.models import Sequential
from keras.layers import Dense

# Arguments to be given:
# 1: The model specifications (numbers after model- or weights-)
# 2: The file from which predictions must be made
# Example input: "48s_38s_28s_18s_28s_38s_47_mean_squared_error_adam" "0_1.txt"
modelSpecs = sys.argv[1]  # if defined -> train
predictionData = sys.argv[2]
DATAPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), os.pardir, os.pardir, "res", "data", "preprocessed", predictionData))
(inputObject, outputObject) = readJsonData(DATAPATH)

# Define paths and data
RESULTSPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "activationValues"))
resultsName = "actVals_"+modelSpecs+".txt"

model = loadModel(modelSpecs)
loadModelWeights(modelSpecs, model)

if os.path.exists(DATAPATH) and predictionData != "":
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

    predictions = halfModel.predict(inputObject, batch_size=1, verbose=1)
    print("send data:")
    print("State:")
    print(predictions[0])
    print("Reward:")
    print(inputObject[0][0])
    # for i in range(0, len(predictions)):
    #     run(predictions[i], inputData[i][0])
    # save()
else:
    print("The data from which predictions must be made does not exist")