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
data = sys.argv[1] # if defined -> train
originalVars = 8
varsPerObs = 4
observations = int(sys.argv[2])
epochs = int(sys.argv[3])
makePrediction=True

# Define paths and data
MODELPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "model"))
WEIGHTSPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "weights"))
RESULTSPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "results"))
modelName = "model-"+str(observations)+".json"
weightsName = "weights-"+str(observations)+".h5"
resultsName = "weights-"+str(observations)+".txt"
DATAPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), os.pardir, os.pardir, "res", "data", "preprocessed", data))
if os.path.exists(DATAPATH) and data !="" :
    train = True
else: train = False

batch_size = 1
epsilon_std = 1.0
printTraining=1

# Read or create the model
if Path(os.path.join(MODELPATH,modelName)).exists() :
    json_file = open(os.path.join(MODELPATH,modelName), 'r')
    loaded_model_json = json_file.read()
    json_file.close()
    model = model_from_json(loaded_model_json)
else:
    input_dim = originalVars + (varsPerObs * observations)
    output_dim = input_dim - 1
    intermediate_dim = 8+observations
    latent_dim = 3
    model = Sequential()
    model.add(Dense(intermediate_dim, input_dim=input_dim, activation='relu'))
    model.add(Dense(latent_dim, activation='relu'))
    model.add(Dense(intermediate_dim, activation='relu'))
    model.add(Dense(output_dim, activation='relu'))

    model.compile(loss="mean_squared_error", optimizer="adam", metrics=["accuracy"])

    model_json = model.to_json()
    # Modelname: model-observations.json
    with open(os.path.join(MODELPATH, modelName), "w") as json_file:
        json_file.write(model_json)

# Set weights if already existing
if Path(os.path.join(WEIGHTSPATH,weightsName)).exists() :
    model.load_weights(os.path.join(WEIGHTSPATH, weightsName))

if train:
    model.compile(loss="mean_squared_error", optimizer="adam", metrics=["accuracy"])
    text_file = open(DATAPATH, "r")
    lines = text_file.readlines()
    # Create the training data
    # Empty data object
    input_dim = originalVars + (varsPerObs * observations)
    output_dim = input_dim - 1
    inputData = np.zeros((math.floor(len(lines)/(observations+3)), input_dim), dtype=np.float64)
    outputData = np.zeros((math.floor(len(lines)/(observations+3))-1, output_dim), dtype=np.float64)

    # Remove unnecessary '\n'
    for i in range(0, len(lines)) :
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
    for i in range(0,len(lines),observations+3):
        iteration = int((i / (observations + 3)))
        objects = lines[i+1].split(";")
        inputData[iteration][len(inputData[iteration])-1]=objects[len(objects)-1]
        objects = objects[0:len(objects)-1]
        inputData[iteration][0:len(objects)]=objects[0:len(objects)]
        for j in range(0,observations):
            obsObjects = lines[i+2+j].split(";")
            inputData[iteration][(len(objects)+(j*4)+0):(len(objects)+(j*4)+4)] = obsObjects[0:4]
        if iteration>0:
            outputData[iteration-1][0:(len(objects)+observations*4)]=inputData[iteration][0:(len(objects)+observations*4)]
    inputData = inputData[0:len(inputData)-1]

    model.fit(inputData, outputData, epochs=epochs, batch_size=batch_size, verbose=printTraining)

    model.save_weights(os.path.join(WEIGHTSPATH,weightsName))
else:
    np.set_printoptions(precision=16, linewidth=1000)
    weightsFile = open(os.path.join(RESULTSPATH,resultsName), "w")
    for i in range(0, len(model.layers)):
        totalWeights = np.insert(model.layers[i].get_weights()[0], 0, model.layers[i].get_weights()[1], axis=0)
        for j in range(0, len(totalWeights)):
            weightsFile.write(str(totalWeights[j].tolist())+"\n")
        weightsFile.write("\n")



if makePrediction:
    test = np.zeros((1,originalVars+observations*varsPerObs), dtype=np.float64)
    text_file = open(os.path.join(DATAPATH, "0_0.txt"), "r")
    lines = text_file.readlines()
    # Create the training data
    # Empty data object
    input_dim = originalVars + (varsPerObs * observations)

    # Remove unnecessary '\n'
    for i in range(0, len(lines)) :
        if lines[i].endswith("\n"):
                lines[i] = lines[i][:-1]

    for i in range(0,len(lines),len(lines)):
        iteration = int((i / (observations + 3)))
        objects = lines[i+1].split(";")
        test[iteration][len(test[iteration])-1]=objects[len(objects)-1]
        objects = objects[0:len(objects)-1]
        test[iteration][0:len(objects)]=objects[0:len(objects)]
        for j in range(0,observations):
            obsObjects = lines[i+2+j].split(";")
            test[iteration][(len(objects)+(j*4)+0):(len(objects)+(j*4)+4)] = obsObjects[0:4]

    print(model.predict(test, 1, 1))

quit()




















#
# # Input layer
# x = Input(shape=(input_dim,))
#
# h = Dense(intermediate_dim, activation='relu')(x)
# z_mean = Dense(latent_dim)(h)
# z_log_var = Dense(latent_dim)(h)
#
#
# def sampling(args):
#     z_mean, z_log_var = args
#     epsilon = K.random_normal(shape=(K.shape(z_mean)[0], latent_dim), mean=0.,
#                               stddev=epsilon_std)
#     return z_mean + K.exp(z_log_var / 2) * epsilon
#
#
# # note that "output_shape" isn't necessary with the TensorFlow backend
# z = Lambda(sampling, output_shape=(latent_dim,))([z_mean, z_log_var])
#
# # we instantiate these layers separately so as to reuse them later
# decoder_h = Dense(intermediate_dim, activation='relu')
# decoder_mean = Dense(input_dim, activation='relu')
# h_decoded = decoder_h(z)
# x_decoded_mean = decoder_mean(h_decoded)
#
#
# # Custom loss layer
# class CustomVariationalLayer(Layer):
#     def __init__(self, **kwargs):
#         self.is_placeholder = True
#         super(CustomVariationalLayer, self).__init__(**kwargs)
#
#     def vae_loss(self, x, x_decoded_mean):
#         xent_loss = input_dim * metrics.binary_crossentropy(x, x_decoded_mean)
#         kl_loss = - 0.5 * K.sum(1 + z_log_var - K.square(z_mean) - K.exp(z_log_var), axis=-1)
#         return K.mean(xent_loss + kl_loss)
#
#     def call(self, inputs):
#         x = inputs[0]
#         x_decoded_mean = inputs[1]
#         loss = self.vae_loss(x, x_decoded_mean)
#         self.add_loss(loss, inputs=inputs)
#         # We won't actually use the output.
#         return x
#
#
# # Empty data object
# inputData = np.zeros((math.floor(len(lines)/(observations+3)), input_dim), dtype=np.float64)
# outputData = np.zeros((math.floor(len(lines)/(observations+3))-1, input_dim), dtype=np.float64)
#
# for i in range(0, len(lines)) :
#     if lines[i].endswith("\n"):
#             lines[i] = lines[i][:-1]
#
# # Fill in input and output
# # 0: avatarSpeed
# # 1: avatarHealthPoints
# # 2: currentAvatarOrientation-width
# # 3: currentAvatarOrientation-height
# # 4: currentAvatarPosition-width
# # 5: currentAvatarPosition-height
# # 6+4*i: observation category
# # 7+4*i: observation x-coordinate
# # 8+4*i: observation y-coordinate
# # 9+4*i: Euclidian distance to avatar
# # 9+4*max(i)+1: action (input only)
# for i in range(0,len(lines),observations+3):
#     iteration = int((i / (observations + 3)))
#     objects = lines[i+1].split(";")
#     score=objects[0]
#     inputData[iteration][len(inputData[iteration])-1]=objects[len(objects)-1]
#     objects = objects[0:len(objects)-1]
#     inputData[iteration][0:len(objects)]=objects[0:len(objects)]
#     for j in range(0,observations):
#         obsObjects = lines[i+2+j].split(";")
#         inputData[iteration][(len(objects)+(j*4)+0):(len(objects)+(j*4)+4)] = obsObjects[0:4]
#     if iteration>0:
#         outputData[iteration-1][0:(len(objects)+observations*4)]=inputData[iteration][0:(len(objects)+observations*4)]
#         outputData[iteration-1][len(outputData[iteration-1])-1]=score
# inputData = inputData[0:len(inputData)-1]
#
# y = CustomVariationalLayer()([x, x_decoded_mean])
# vae = Model(x, y)
# vae.compile(optimizer='rmsprop', loss='mean_squared_error')
#
#
# # train the VAE on MNIST digits
# # (x_train, y_train), (x_test, y_test) = mnist.load_data()
#
#
#
# # x_train = x_train.astype('float32') / 255.
# # x_test = x_test.astype('float32') / 255.
# # x_train = x_train.reshape((len(x_train), np.prod(x_train.shape[1:])))
# # x_test = x_test.reshape((len(x_test), np.prod(x_test.shape[1:])))
# train = sys.argv[3]
# WEIGHTPATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "weights", "encoderWeights.h5"))
#
# if train !="True" and os.path.isfile(WEIGHTPATH):
#     vae.load_weights(WEIGHTPATH)
# elif train=="True" or not (os.path.isfile(WEIGHTPATH)):
#     vae.fit(x=inputData,
#             y=outputData,
#             shuffle=True,
#             epochs=epochs,
#             batch_size=batch_size,
#             verbose=1)
#     print("weights save to: " + WEIGHTPATH)
#     vae.save_weights(WEIGHTPATH, overwrite=True)
#
#
# # build a model to project inputs on the latent space
# encoder = Model(x, z_mean)
# print(vae.layers)
# quit()
#
# # display a 2D plot of the digit classes in the latent space
# x_test_encoded = encoder.predict(inputData, batch_size=batch_size)
# plt.figure(figsize=(6, 6))
# plt.scatter(x_test_encoded[:, 0], x_test_encoded[:, 1], c=outputData)
# plt.colorbar()
# plt.show()
#
#
# # build a digit generator that can sample from the learned distribution
# decoder_input = Input(shape=(latent_dim,))
# _h_decoded = decoder_h(decoder_input)
# _x_decoded_mean = decoder_mean(_h_decoded)
# generator = Model(decoder_input, _x_decoded_mean)
#
# # display a 2D manifold of the digits
# n = 15  # figure with 15x15 digits
# digit_size = 28
# figure = np.zeros((digit_size * n, digit_size * n))
# # linearly spaced coordinates on the unit square were transformed through the inverse CDF (ppf) of the Gaussian
# # to produce values of the latent variables z, since the prior of the latent space is Gaussian
# grid_x = norm.ppf(np.linspace(0.05, 0.95, n))
# grid_y = norm.ppf(np.linspace(0.05, 0.95, n))
#
# for i, yi in enumerate(grid_x):
#     for j, xi in enumerate(grid_y):
#         z_sample = np.array([[xi, yi]])
#         x_decoded = generator.predict(z_sample)
#         digit = x_decoded[0].reshape(digit_size, digit_size)
#         figure[i * digit_size: (i + 1) * digit_size,
#                j * digit_size: (j + 1) * digit_size] = digit
#
# plt.figure(figsize=(10, 10))
# plt.imshow(figure, cmap='Greys_r')
# plt.show()
#
#
#
#
#
#
