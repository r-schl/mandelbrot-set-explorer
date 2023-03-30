# Mandelbrot Set Explorer
#### This program was created as part of a school assignment on the topic of [calculation and visualization of the Mandelbrot set](https://github.com/r-schl/mandelbrot-set-paper)
The Mandelbrot set $\mathbb {M}$ (see [Wikipedia](https://en.wikipedia.org/wiki/Mandelbrot_set))  is a set of complex numbers $c \in \mathbb {C}$ for which the sequence $\(z_{c,n}\)$ with $z_{c,n}=z^2+c$ does not diverge to infinity when iterated from $z=0$. This is a program that visualizes the Mandelbrot set. It allows the user to zoom into the set, change the coloring and save images of the set, among other things. 

This application runs on Java 11.0.6. 

### What is $n_{max}$ and how is $\mathbb {M}$ calculated?

To a certain extent, the number $n_{max} \in \mathbb {N}$ determines the accuracy of the calculation. 

Let $c \in \mathbb {C}$. To find out whether $c \in \mathbb {M}$, it must be checked whether the sequence $\(z_{c,n}\)$ remains bounded. To do so, this application tries to calculate the sequence starting from $z_{c,0}$ up to $z_{c,n_{max}}$. If during this calculation there where any $n < n_{max}$ so that $|z_{c,n}| > 2$, this algorithm will terminate prematurely. Because in that case it is proven that $\(z_{c,n}\)$ will definitely diverge to infinity (for proof, see [explanation of Mike Hurley](http://mrob.com/pub/muency/escaperadius.html)). On the other hand if this calculation reaches $z_{c,n_{max}}$, meaning $|z_{c,n}| \leq 2$ for all $n < n_{max}$, this application will assume that $c \in \mathbb {M}$. However, this assumption is by no means exact, especially in the marginal areas of the Mandelbrot set. Because there are always some $c \notin \mathbb {M}$ for which $|z_{c,n}|$ will not be greater than 2 until $z_{c,k}$ ($k > n_{max}$) has been calculated. Thus, as $n_{max}$ is increased, more and more numbers which were previously erroneously assumed to be in the set are discarded. 

### How is the image colored?

Let $c \in {C}$. To visualize $\mathbb {M}$ as a mathematical set, only two colors are needed: one for $c \in \mathbb {M}$ and one for $c \notin \mathbb {M}$. But a two-tone image is boring. Therefore in this application one can specify one color $c \in \mathbb {M}$ and up to five colors that from a color gradient for $c \notin \mathbb {M}$. 

Let $c \notin \mathbb {M}$ and let $z_{c,k}$ with $k \in \mathbb {N}$ be first element in the sequence for which $z_{c,k} > 2$.  



### How to use this application

#### Overview

The program window is divided into three parts. At the top of the window is the menu bar in which all control elements of the program are located. Below that is the screen, which takes up the most space. There, the part of the Mandelbrot set within the view port are displayed. A status bar can be seen at the bottom of the window. It displays information about the program, the progress of the calculation and the current zoom factor. 

#### Menu bar

The first area of the menu bar allows saving the settings made by the user. This includes the view port, the colors of the color palette and the maximum number of iterations $n_{max}$. This saves the project for later use. A previously saved file can also be loaded again. In this case the settings described in the file are transferred to the program. In addition to the first two buttons, there is another button in this area that allows the user to save what is displayed as an image. A dialog window will then open in which the size of the image and the storage location must be specified by the user. 

In a second area, by clicking on the button labeled "Bearbeiten" the coloring of the Mandelbrot set can be configured. A dialog window will appear in which both a color for the Mandelbrot set itself can be specified, and up to five other colors can also be specified, which make up a color gradient for all points outside of the set. Not all five colors have to be set. For example, if only one color is specified for the gradient, a two-tone image emerges. 

The maximum number of iterations $n_{max}$ can be set in the next area. If this value is gradually increased, it is easy to observe how the accuracy of calculating of the Mandelbrot set increases. 

In the fourth area of the menu bar, settings can be made that affect the view port. The values $c_{min}$ and $c_{max}$ that determine the view port can be edited in a dialog window that one can open by clicking on the button with the inscription "Ändern". The smaller square buttons allow you to zoom in, zoom out and to set the view port to default. Next to it there is a checkbox which determines whether if the window is resized, the view port should remain locked or change to accommodate the resizing. 
