# Projet-Covid-19

0. S'assurer des installations :
	- JDK 11 ou +
	- MySQL / Xampp
	- MongoDBCompassCommunity
	- NodeJS 14.17.3 LTS
	- Angular/cli 13.2.3 LTS
	- npm 7.5.6
	- nodemon -g

1. Configurer les adresses des micro-services

	1.1. Service 'SecuriteService' : dans le fichier application.yml ---> 
		
		1.1.1. Mettre l'IP de votre machine - Utiliser l'IP configurée ici dans le Serveur NodeJS
	
	
	1.2. Service 'SchedulerService' : dans le fichier application.yml --->
		
		1.2.1. Mettre l'IP de votre machine - Utiliser l'IP configurée ici dans le Serveur NodeJS
	
	1.3. Service NodeJS 'node-server' : dans index.js, mettre le port que vous souhaitez, sinon laisser le '5000' actuel


2. Installer les librairies Javascription des services javascript :
	
	2.1. Service NodeJS 'node-server' : dans le dossier exécuter la commande 'npm install' (npm i)
	
	4.2. Le client angular 'client-covid-19' : dans le dossier exécuter la commande 'npm install' (npm i)


3. Exécuter les projets :
	3.1. Cas des projets JavaScript
		
		3.1.1. 'node-server' : la commande 'npm start' ou 'node index.js' ---> Les options actuelles feront écouter le serveur dans le port 5000

	----
		
		3.1.2. 'client-covid-19' : la commande 'ng serve' ---> son serveur ecoutera dans le port par défaut 4200

	3.2. Cas des projets Java : le plus simple de les ouvrir dans un IDE comme Eclipse, sinon générer des Jars exécutables

4. Une fois tous les services lancés, ouvrir, dans le navigateur web, l'adresse 'http://localhost:4200'
