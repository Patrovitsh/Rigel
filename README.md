# Projet - Rigel - Juin 2020
Conception d'un programme illustrant la carte des étoiles. À noter que ce programme a été réalisé dans le cadre d'un 
cours de première année à l'Ecole Polytechnique Fédérale de Lausanne en Suisse.

## Présentation
Le projet Rigel est un programme dynamique permettant de représenter la carte des étoiles depuis n'importe quel point 
du globe à n'importe quel moment. Il est possible de déplacer la vue de l'observateur, de laisser le temps s'écouler et
voir la carte des étoiles bouger, ou encore de suivre le parcours des astres célestes. 

## Commandes
Les flèches directionnelles `Left`, `Right`, `Up` et `Down` permettent de déplacer la vue. Les autres contrôles se font
à l'aide des boutons sur l'interface ou le Menu.

## Menu :
Sur la barre de contrôle, nous avons ajouté un nouveau bouton nommé « Menu » et offrant des nouveaux choix : Carte 
(changer la position d’observation), Accélérateur, Traqueur (suivre un objet céleste), Affichage, Paramètres et 
Quitter. Chaque MenuItem ouvre une nouvelle fenêtre (sauf Quitter) qui est non redimensionnable sauf pour Carte. 
Cliquer sur le Canvas du ciel, ferme les fenêtres, comme la croix rouge. Les fenêtres sont codées dans Main.

### - Carte
Ouvre une fenêtre contenant la carte du monde. On peut changer la taille de cette fenêtre de façon que la carte se 
dessine sur un carré de côté égal à la plus petite de la largeur et longueur de la fenêtre, et se positionne au milieu
de l’autre. Sur cette carte une petite épingle est positionnée par défaut sur la dernière position géographique 
enregistrée. Pour changer de position d’observation il suffit de cliquer sur l’endroit désiré. Une nouvelle punaise 
sera dessinée et les coordonnées géographiques définies.

### - Accélérateur
Ouvre une fenêtre contenant une CheckBox et un Slider. Si la CheckBox est activé, elle définit l’accélérateur de temps
sur la vitesse contenu dans le Slider, et désactive le sélecteur d’accélérateur de la barre de contrôle. Il vous suffit
d’appuyer sur Play pour lancer la simulation.

### - Traqueur d'objet céleste
En cliquant sur Traqueur dans le menu, on pourra choisir l’objet céleste désiré. Après en activant « suivre un objet 
céleste », le centre d’observation sera centré sur lui tant que la case décrite ci-dessus est coché. Les touches pour 
déplacer la vue sont alors désactivées. Si vous lancez une simulation du temps, vous pourrez voir la trajectoire de 
l’objet céleste se dessiner en violet. Pour obtenir la position de l’objet céleste, il y a une méthode 
getHorCoordsCelestialObject(String name) dans ObservedSky qui retourne la position de l’objet céleste nommé « name ».

### - Affichage
Ouvre une fenêtre contenant plusieurs CheckBox permettant de contrôler ce que l’on veut dessiner sur le Canvas. La 
propriété de chaque CheckBox est reliée à une propriété booléenne dans le SkyCanvasManager. Ainsi, lorsqu’on souhaite 
dessiner le ciel, on évalue les propriétés pour savoir quoi dessiner.

### - Paramètres
Ouvre une fenêtre contenant trois Sliders qui sont chacun liés à une propriété de type double du SkyCanvasManager. Ces 
propriétés reflètent un facteur à appliquer lorsqu’on veut déplacer notre vue ou zoomer. Cela permet en d’autres 
termes, de régler la sensibilité des touches et de la molette.

### - Quitter 
Quitte le programme.

## Dessin des constellations (imagé, par exemple la grande ours) :
En activant les constellations imagées dans la fenêtre « Affichage » (activés par défaut), vous pourrez voir trois 
images de constellations : la petite ourse, la grande ourse et Orion. Seulement trois constellations sont dispoibles 
dans ce mode par manque de temps. Les images ont été obtenues en retouchant des photos existantes avec Gimp. La
position de l’image est déterminée à partir de la position d’une étoile, l’ange de rotation en fonction de l’angle 
formé par deux étoiles et l’horizontale plus une constante arbitraire. La taille de l’image est calculée à partir de la
distance entre les deux étoiles, multiplié par une constante arbitraire.

## Cycle Jour/Nuit :
En activant cycle jour/nuit dans la fenêtre « Affichage » (désactivé par défaut). La couleur du Canvas passe en bleu à
chaque fois la position du soleil est au-dessus de l’horizon et repasse en noir si elle est en dessous. La mise en 
œuvre est triviale mais il y a également une toute petite fonction mathématique qui permet de faire varier la couleur 
progressivement, cela est fait dans la méthode clear(…) du SkyCanvasPainter.