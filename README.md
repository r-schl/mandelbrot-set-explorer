# Mandelbrot Set Explorer
#### This program was created as part of a school assignment on the topic of [calculation and visualization of the Mandelbrot set](https://github.com/r-schl/mandelbrot-set-paper)
The Mandelbrot set is a set of complex numbers $c$ for which the function $f_c(z)=z^2+c$ does not diverge to infinity when iterated from $z=0$. This is a program that visualizes the Mandelbrot set. It allows the user to zoom into the set, change the coloring and save images of the set, among other things. 

This application runs on Java 11.0.6. 

### How to use this application

#### Overview

The program window is divided into three parts. At the top of the window is the menu bar in which all control elements of the program are located. Below that is the screen, which takes up the most space. There, the part of the Mandelbrot set within the view port are displayed. A status bar can be seen at the bottom of the window. It displays information about the program, the progress of the calculation and the current zoom factor. 

#### Menu bar

The first area of the menu bar allows saving the settings made by the user. This includes the view port, the colors of the color palette and the maximum number of iterations $n_max$. This saves the project for later use. A previously saved file can also be loaded again. In this case the settings described in the file are transferred to the program. In addition to the first two buttons, there is another button in this area that allows the user to save what is displayed as an image. A dialog window will then open in which the size of the image and the storage location must be specified by the user. 

In a second area, by clicking on the button labeled "Bearbeiten" the coloring of the Mandelbrot set can be configured. A dialog window will appear in which both a color for the Mandelbrot set itself can be specified, and up to five other colors can also be specified, which make up a color gradient for all points outside of the set. Not all five colors have to be set. For example, if only one color is specified for the gradient, a two-tone image emerges. 

The maximum number of iterations $n_max$ can be set in the next area. If this value is gradually increased, it is easy to observe how the accuracy of calculating of the Mandelbrot set increases. 

In the fourth area of the menu bar, settings can be made that affect the view port. The values $c_min$ and $c_max$ that determine the view port can be edited in a dialog window that one can open by clicking on the button with the inscription "Ändern". The smaller square buttons allow you to zoom in, zoom out and to set the view port to default. Next to it there is a checkbox which determines whether if the window is resized, the view port should remain locked or change to accommodate the resizing. 
