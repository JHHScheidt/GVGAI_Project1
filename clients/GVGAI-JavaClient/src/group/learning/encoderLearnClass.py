import os
import sys
from pathlib import Path
import math
from encoderGeneralFunctions import readJsonData2, loadModel, loadModelWeights, readFolderData2

from keras.models import Sequential
from keras.layers import Dense

class learner:
    
    activations= ["sigmoid", "sigmoid", "sigmoid", "sigmoid", "sigmoid", "sigmoid", "sigmoid", "sigmoid", "sigmoid"]
    loss = "mean_squared_error"
    optimizer = "adam" 
    epochs=1
    modelRepresentation =""
    WEIGHTSPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "weights"))
    
    model = None
    
    def createLoadModel(self, deicticViewSize):
        global model
        #DATAPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), os.pardir, os.pardir, "res", "data", "preprocessed"))
        MODELPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "model"))
        totalVars = 1+1+1+2+2+deicticViewSize*(1+1+2)
        
        # Define model representation
        dimensions = [
            totalVars,
            43,
            38,
            33,
            28,
            23,
            18,
            13,
            8,
            5
        ]
        
        self.modelRepresentation = "_"
        for i in range(0, len(dimensions)-1):
            self.modelRepresentation = self.modelRepresentation+str(dimensions[i])+self.activations[i][0]+"_"
        self.modelRepresentation = self.modelRepresentation+str(dimensions[len(dimensions)-1])+"_"+self.loss+"_"+self.optimizer
        
        # Read or create the model
        print(MODELPATH)
        if Path(os.path.join(MODELPATH, "model"+self.modelRepresentation+".json")).exists():
            self.model = loadModel(self.modelRepresentation)
        else:
            self.model = Sequential()
            for i in range(1, len(dimensions)):
                if i == 1:
                    self.model.add(Dense(dimensions[i], input_dim=dimensions[0], activation=self.activations[i-1]))
                else:
                    self.model.add(Dense(dimensions[i], activation=self.activations[i-1]))
        
            self.model.compile(loss=self.loss, optimizer=self.optimizer, metrics=["accuracy"])
        
            model_json = self.model.to_json()
            with open(os.path.join(MODELPATH, "model"+self.modelRepresentation+".json"), "w") as json_file:
                json_file.write(model_json)
        
        # Set weights if already existing
        if Path(os.path.join(self.WEIGHTSPATH, "weights"+self.modelRepresentation+".h5")).exists():
            loadModelWeights(self.modelRepresentation, self.model)
    
    def learn(self, inputObject, outputObject):
        #define these variables as global..
        
        self.model.compile(loss=self.loss, optimizer=self.optimizer, metrics=["accuracy"])
    
        self.model.fit(inputObject, outputObject, epochs=self.epochs, batch_size=1, verbose=0)
    
        self.model.save_weights(os.path.join(self.WEIGHTSPATH, "weights"+self.modelRepresentation+".h5"))
        
    def calculate(self, inputObject):
        predictions = self.model.predict(inputObject, batch_size=1, verbose=0)
        return predictions[0]
    
    
    
    def learnOld(self, input,output):
        # Arguments to be given:
        # 1: the text file from which data is read
        # 2: Number of epochs
        # Example input: "0_1.txt" 1000
        #data = sys.argv[1]
        epochs = 0
        
        # Receive input/output data
        DATAPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), os.pardir, os.pardir, "res", "data", "preprocessed"))
        
        #if(DATAPATH[len(DATAPATH)-4:len(DATAPATH)] ==".txt"):
        #    (inputObject, outputObject) = readJsonData2(DATAPATH)
        #elif Path(DATAPATH).exists():
        #    (inputObject, outputObject) = readFolderData2(DATAPATH)
        #else:
        #    print("The given data file is neither a text file nor an existing folder")
        
        inputObject=input
        outputObject= output
        # Define paths and data
        MODELPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "model"))
        WEIGHTSPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "weights"))
        
        # Define model representation
        dimensions = [
            len(inputObject[0]),
            43,
            38,
            33,
            28,
            23,
            18,
            13,
            8,
            len(outputObject[0])
        ]
        activations = ["sigmoid", "sigmoid", "sigmoid", "sigmoid", "sigmoid", "sigmoid", "sigmoid", "sigmoid", "sigmoid"]
        loss = "mean_squared_error"
        optimizer = "adam"
        
        modelRepresentation = "_"
        for i in range(0, len(dimensions)-1):
            modelRepresentation = modelRepresentation+str(dimensions[i])+activations[i][0]+"_"
        modelRepresentation = modelRepresentation+str(dimensions[len(dimensions)-1])+"_"+loss+"_"+optimizer
        
        # Read or create the model
        if Path(os.path.join(MODELPATH, "model"+modelRepresentation+".json")).exists():
            model = loadModel(modelRepresentation)
        else:
            model = Sequential()
            for i in range(1, len(dimensions)):
                if i == 1:
                    model.add(Dense(dimensions[i], input_dim=dimensions[0], activation=activations[i-1]))
                else:
                    model.add(Dense(dimensions[i], activation=activations[i-1]))
        
            model.compile(loss=loss, optimizer=optimizer, metrics=["accuracy"])
        
            model_json = model.to_json()
            with open(os.path.join(MODELPATH, "model"+modelRepresentation+".json"), "w") as json_file:
                json_file.write(model_json)
        
        # Set weights if already existing
        if Path(os.path.join(WEIGHTSPATH, "weights"+modelRepresentation+".h5")).exists():
            loadModelWeights(modelRepresentation, model)
        
        if os.path.exists(DATAPATH):
            model.compile(loss=loss, optimizer=optimizer, metrics=["accuracy"])
        
            model.fit(inputObject, outputObject, epochs=epochs, batch_size=1, verbose=1)
        
            model.save_weights(os.path.join(WEIGHTSPATH, "weights"+modelRepresentation+".h5"))
        else:
            print("The given data does not exist in '\\res\\data\\preprocessed\\...'")
