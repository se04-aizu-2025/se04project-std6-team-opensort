# CUI

## 1) Launching the software
After launching the cui, the array to be sorted and the algorithm to use need to be configured.

### a) Array input
If no initial array was provided when launching the program, the user will be requested to input a custom array:
```
Please enter a ',' separated list of integers:
```
As seen in the example output, the array must be specified as a comma separated list of integers, like ```5,4,3,2,1```.  
If an invalid input is provided, an error will be reported and the user is asked to input the array again.  
This can be aborted by pressing __CRTL-C__.

### b) Selecting a sorting algorithm
Afterward, the user will be prompted to select a sorting algorithm to use:
```
1: Heap sort
2: Selection sort
3: Quick sort
4: Insertion sort
Select an algorithm (0-4):
```
This can be done by inputting the corresponding number.  
If an invalid input is provided, an error will be reported and the user is asked to input the number again.  
This can be aborted by pressing __CRTL-C__.

## 2) Using the cui
The cui can be navigated using user command inputs.  
The following command guide can be displayed by typing _help_:
```
Available commands:
    - (n)ext:
        Perform next step
    - (p)rint:
        Print the array
    - (h)elp | ?:
        Print this help
    - (q)uit | (e)xit:
        Quit the program
    - algo:
        Change the sorting algorithm
    - arr:
        Change the array being sorted
```
The commands _arr_ and _algo_ use the same methods as described in __1)__ and __2)__ to select a new array or algorithm.

### Array output
When printing the array or stepping through the algorithms steps, the following pattern is used to output the array:
```
[Status message]
[Array]
[Markers]
```
| Name           | Description                                                                |
|----------------|----------------------------------------------------------------------------|
| Status message | A description of the currently performed step in the algorithm.            |
| Array          | The current state of the array.                                            |
| Markers        | Markers that help visualize the currently performed step in the algorithm. |

The following markers are currently being used:

| Marker | Description                                                                  |
|--------|------------------------------------------------------------------------------|
| *      | Marks the current element as sorted                                          |
| ?      | Shows that the marked elements are being compared                            |
| ^      | The marked elements where swapped.                                           |
| !      | The element was hilighted. More details are contained in the status message. |