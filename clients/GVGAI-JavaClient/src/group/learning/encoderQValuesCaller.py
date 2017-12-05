import os
import sys
# from QValues import run, save
from encoderGeneralFunctions import readJsonData, loadModel, loadModelWeights

# Arguments to be given:
# 1: The model specifications (numbers after model- or weights-)
# 2: The file from which predictions must be made
# Example input: "48s_38s_28s_18s_28s_38s_47_mean_squared_error_adam" "0_1.txt"
modelSpecs = sys.argv[1]  # if defined -> train
predictionData = sys.argv[2]
DATAPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), os.pardir, os.pardir, "res", "data", "preprocessed", predictionData))
(inputObject, outputObject) = readJsonData(DATAPATH)

# Define paths and data
# RESULTSPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "activationValues"))
# resultsName = "actVals_"+modelSpecs+".txt"

model = loadModel(modelSpecs)
loadModelWeights(modelSpecs, model)

if os.path.exists(DATAPATH) and predictionData != "":
    predictions = model.predict(inputObject, batch_size=1, verbose=1)
    for i in range(0, len(inputObject)):
        print("Actual input:")
        print(inputObject[i])
        print("Predicted output of next iteration:")
        print(predictions[i])
    # for i in range(0, len(predictions)):
    #     run(predictions[i], inputData[i][0])
    # save()
else:
    print("The data from which predictions must be made does not exist")