# Introduction to clj-uclassify

clj-uclassify supports all the operations provided by the uClassify
web service. It provides it's functionality with the following
functions:
* create-classifier : For creating new classifiers
* add-class : For adding new classes in the classifiers
* remove-class : For removing existing classes in the classifiers
* remove-classifier: For removing existing classifier
* train : To train the classifier on a text for a specified class
* untrain : To untrain the classifier on a text for a specified class
* get-information : Gets information about the classifier
* classify : Sends a text to a classifier and returns a classification
* classify-keywords : Sends a text to a classifier and returns a
  classification and relevant keywords for each class.

## Usage examples:

Before using any of the examples, you need to get API keys. Just
signup an account [here](http://www.uclassify.com/Register.aspx) and
get the keys before playing with this library.

All of the clj-uclassify functions takes the keys as the first
parameter. The keys should be passed down as a *map*. The best way is to
store the key in a variable like this:

`(def api-keys {:read-key "********" :write-key "*******"})`

Note that the keys *read-key* and *write-key* should not be changed to
other names in your map structure. Also if a call fails, then it will
throw an exception describing why it failed. (This behavior can be
easily changed by changing check-response function.)

Then you can use the variable *api-keys* in all of your subsequent calls.

### create-classifier

`(create-classifier api-keys "sample-classifier")`

The above call will create a classifier named *sample-classifier*.

### add-class

`(add-class api-keys "sample-classifier" '("class1" "class2"))`

The above call will add classes *class1* and *class2* for the
classifier named *sample-classifier*.

### remove-class

`(remove-class api-keys "sample-classifier" '("class1" "class2"))`

The above call will remove classes *class1* and *class2* for the
classifier named *sample-classifier*.

### remove-classifier

`(remove-classifier api-keys "existing-classifier")`

The above call will remove the entire classifier *existing-classifier*. 

### train

`(train api-keys '("I like cosmetic" "That is so hot") "class1"
"sample-classifier")`

The above call will train both the texts for the class *class1* in the
classifier *sample-classifier*

### untrain

`(untrain api-keys '("I like cosmetic" "That is so hot") "class1"
"sample-classifier")`

The above call will untrain both the texts for the class *class1* in
the classifier *sample-classifier*

### get-information

`(get-information api-keys "sample-classifier")`

The above call will return `(("class1" "class2") ("0" "0") ("0" "0"))`

It represents that the *class1* and *class2* has zero unique features
and total counts.

### classify

`(classify api-keys '("hi" "bye") "sample-classifier")`

The above call will return `(("0" "0") (["class1" "0.5"]
["class2" "0.5"]) (["class1" "0.5"] ["class2" "0.5"]))`

The first list `("0" "0")` indicates the text coverage of *class1* and
*class2* in the classifier *sample-classifier*.
The next list represents the classification result. i.e. the text "hi"
belongs to *class1* with a probability of *0.5*.

Another example with a classifier published in another account:

`(classify api-keys '("that cosmetic is so nice" "I like to code.")
"GenderAnalyzer_v5" "uClassify")`

In the above example we are using the classifier named
[GenderAnalyzer_v5](http://www.uclassify.com/browse/uClassify/GenderAnalyzer_v5)
published under the
[uClassify](http://www.uclassify.com/browse/uClassify) account.

The above call returns `(("1" "1") (["female" "0.844033"]
["male" "0.155967"]) (["female" "0.146065"] ["male" "0.853935"]))`

It classifies the text "that cosmetic is so nice" to the class
*female* with the probability of 0.844033 and to the class *male* with
the probability of *0.155967*.

### classify-keywords

`(classify-keywords api-keys '("that cosmetic is so nice" "I like to
code") "GenderAnalyzer_v5" "uClassify")`

The above call returns `(("1" "1")
(["female" "0.844033" "cosmetic so nice"]
["male" "0.155967" "is that"]) (["female" "0.285969" "I like to"]
["male" "0.714031" "code"]))`

This call is the same as classify function but also returns the
relevant keywords for each class. This function should be used when
you want the keywords for a text.
