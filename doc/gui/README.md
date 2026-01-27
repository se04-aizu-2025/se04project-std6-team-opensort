# GUI

## 1) Launching the software
When you open the GUI window, the program checks if there is data to sort.

### a) Array input
If there is no data, a box will appear asking you to choose:
* **Input Custom Data:** You can type your own numbers, separated by commas (e.g., `5, 1, 4, 2`).
* **Generate Random:** The DataGenerator will make a random list for you.

### b) Selecting a sorting algorithm
To start, you must choose an algorithm from the top menu:

`Algorithm > Bubble Sort / Quick Sort / Heap Sort`

When you pick one, the data sets automatically.

## 2) Using the GUI
The GUI is controlled by buttons at the bottom of the screen.

### Control Panel
The following buttons control the animation:

* **▶ Play / ⏸ Pause**
    * **Play:** Starts the sorting animation.
    * **Pause:** Stops the animation instantly. The sorting waits quietly.
* **⏭ Step**
    * **One Step Mode.**
    * It does exactly **one** comparison or swap, then pauses again.
    * This is good for checking mistakes.
* **Speed (0.5x - 2.5x)**
    * Changes how fast the blocks move.
    * You can change this even while it is running.

### Settings Menu
You can change the data at any time using the **Settings** menu at the top:
* **Randomize Data:** Instantly creates new random numbers.
* **Input Custom Data:** Lets you type specific numbers to test hard cases.

## 3) How it works (Internal Logic)
Computers sort very fast, but humans see slowly. To fix this, we use a **"Queue"** system.
1.  **The Sorter:** The algorithm calculates a step (like a swap). It puts this step into a **Line**.
2.  **The Painter:** The screen takes the step from the Line and draws it.
3.  **The Rule:** The Line is very short (Capacity 1).
    * If the Painter is busy moving a block, the Sorter **must wait**.
    * This keeps the speed perfect and prevents crashing.

## 4) Visual Output
The GUI uses **Colors** to show what is happening to the array.

| Color      | Description                                           |
|------------|-------------------------------------------------------|
| **Blue** | **Default.** The number is untouched.                 |
| **Orange** | **Compare.** The algorithm is looking at these two numbers to see which is bigger. |
| **Red** | **Swap.** The two numbers are changing places.        |
| **Green** | **Sorted.** This number is in its final, correct place. |

### Smooth Movement
Unlike the CUI, the GUI does not just print the array.
* When numbers swap, they **slide** to their new place.
* We use a "Copy" of the array to draw. This stops glitching.
