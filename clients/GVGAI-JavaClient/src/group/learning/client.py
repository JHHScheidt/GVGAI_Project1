import socket
import time
import numpy as np
from encoderLearnClass import learner
from encoderGeneralFunctions import readJsonDataFromString, getPossibleActions
from test.test_audioop import maxvalues
from _datetime import datetime
 
HOST = "localhost"
PORT = 8080
deicticViewSize=10
myLearner= learner()
myLearner.createLoadModel(deicticViewSize)

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.connect((HOST, PORT))

 #it is not necessary to load the model every time again.. only once in the beginning 

while 1:
    data = sock.recv(4096).decode("UTF-8")
    if(str(data[0]) == "T"):
        #need substring
        print('input-learn: '+data[1:])
        (input, output) =readJsonDataFromString(data[1:],deicticViewSize)
        print(output)
        myLearner.learn(input, output)
        sock.send(b'test\n')
        #format = T State action 
        #one case: receive state+action
        # now do the actual computation with the data just received.
        
        #sock.sendall("just notify?") not necessary?!
    elif(str(data[0])== "E"):
        print('input-calculation: '+data[1:])
        possibleActions= getPossibleActions(data[1:])
        (input, output) =readJsonDataFromString(data[1:], deicticViewSize)
        t= time.time()
        action = myLearner.calculate(input)
        t = time.time()-t
        print("calculation last "+ str(t))
        
        print('output: ', action)
        maxValue=-100000
        returnAction=0
        for i in range(len(action)):
            if i in possibleActions:
                if(action[i]>maxValue):
                    returnAction=i
                    maxValue=action[i]
                    
        ret=str(returnAction)+'\n'
        print('selected action' + ret)
        #format: "E State"
        #another case: get an state and return the best action 
        #return action
        #sock.sendall(action)
        sock.send(ret.encode("UTF-8"))
    elif(data == "Bye\n"):
        sock.close
