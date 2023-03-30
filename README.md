# Mandelbrot Set Explorer
#### This program was created as part of a school assignment on the topic of [calculation and visualization of the Mandelbrot set](https://github.com/r-schl/mandelbrot-set-paper)
The Mandelbrot set $\mathbb {M}$ (see [Wikipedia](https://en.wikipedia.org/wiki/Mandelbrot_set))  is a set of complex numbers $c \in \mathbb {C}$ for which the sequence $\(z_{c,n}\)$ with $z_{c,n+1}=z_{c,n}^2+c$ does not diverge to infinity when iterated from $z_{c,0}=0$. This is a program that visualizes the Mandelbrot set. It allows the user to zoom into the set, change the coloring and save images of the set, among other things. 

This application runs on Java 11.0.6. 

### What is $n_{max}$ and how is $\mathbb {M}$ calculated?

To a certain extent, the number $n_{max} \in \mathbb {N}$ determines the accuracy of the calculation. 

Let $c \in \mathbb {C}$. To find out whether $c \in \mathbb {M}$, it must be checked whether the sequence $\(z_{c,n}\)$ remains bounded. To do so, this application tries to calculate the sequence starting from $z_{c,0}=0$ up to $z_{c,n_{max}}$. If during this calculation there where any $n < n_{max}$ so that $|z_{c,n}| > 2$, this algorithm will terminate prematurely. Because in that case it is proven that $\(z_{c,n}\)$ will definitely diverge to infinity (for proof, see [explanation of Mike Hurley](http://mrob.com/pub/muency/escaperadius.html)). On the other hand if this calculation reaches $z_{c,n_{max}}$, meaning $|z_{c,n}| \leq 2$ for all $n < n_{max}$, this application will assume that $c \in \mathbb {M}$. However, this assumption is by no means exact, especially in the marginal areas of the Mandelbrot set. Because there are always some $c \notin \mathbb {M}$ for which $|z_{c,n}|$ will not be greater than 2 until $z_{c,k}$ ($k > n_{max}$) has been calculated. Thus, as $n_{max}$ is increased, more and more numbers which were previously erroneously assumed to be in the set are discarded. 

### How is the image colored?

Let $c \in {C}$. To visualize $\mathbb {M}$ as a mathematical set, only two colors are needed: one for $c \in \mathbb {M}$ and one for $c \notin \mathbb {M}$. But a two-tone image is boring. Therefore in this application one can specify one color for all $c$ for which $c \in \mathbb {M}$ is assumed and up to five colors that form a color gradient for $c \notin \mathbb {M}$. 

Let $c \notin \mathbb {M}$ and let $z_{c,k} > 2$ with $k \in \mathbb {N}$ be the first element in the sequence which absolute value is greater than 2. Then, according to the algorithm described above, $z_{c,k}$ is the last number that got calculated while iterating $\(z_{c,n}\)$ from $z_{c,0}=0$. Depending on where $k$ is between $0$ and $n_{max}$, a color is picked from the gradient. As a result, the degree of divergence of $\(z_{c,n}\)$ the can be seen in the image. 
