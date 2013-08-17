# Introduction to clj-uclassify

clj-uclassify supports all the operations provided by the uClassify
web service. It provides it's functionality with the following
functions:
* create-classifier : For creating new classifiers
* add-class : For adding new classes in the classifiers
* remove-class : For removing existing classes in the classifiers
* train : To train the classifier on a text for a specified class
* untrain : To untrain the classifier on a text for a specified class
* get-information : Gets information about the classifier
* classify : Sends a text to a classifier and returns a classification
* classify-keywords : Sends a text to a classifier and returns a
  classification and relevant keywords for each class.

