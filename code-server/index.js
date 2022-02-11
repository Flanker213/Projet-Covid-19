const express = require("express");
const cors = require("cors");

const app = express();
app.use(cors());
app.use(express.json());
const PORT = 5000;

// Mieux vaut recupérer l'Url, le typ de la méthode pour mieux rediriger avec moins de code (instructions)
// const request = app.request;
// const method = request.method;
// const url = app.request.url;

const adminSecuriteHost = 'http://127.0.0.1:7575';
const dataStatistiquesHost = 'http://127.0.0.1:7272';

// Le service de securité
// POST 307/303 - Inscription
app.post('/api/covid/v1/comptes-utilisateurs/inscription', (req, res) => {
    // redirection 302 vers le microservice login
    const newUrl = adminSecuriteHost + '/api/covid/v1/comptes-utilisateurs/inscription';
    console.log('********* Redirection vers ADMIN New HTTP URL = ' + newUrl);
    console.log('********* New HTTP Method = ' + req.method);
    res.redirect(307, newUrl);
});
// POST 307/303 - Connexion
app.post('/api/covid/v1/comptes-utilisateurs/connexion', (req, res) => {
    const newUrl = adminSecuriteHost + '/api/covid/v1/comptes-utilisateurs/connexion';
    console.log('********* Redirection vers ADMIN New HTTP URL = ' + newUrl);
    res.redirect(307, newUrl);
});
// POST 307/303 - Connexion de type automatique (par le lien envoyé par mail)
app.post('/api/covid/v1/comptes-utilisateurs/connexion-automatique/**', (req, res) => {
    let url = req.url;
    //url = url.replace();
    const newUrl = adminSecuriteHost + url;
    console.log('********* Redirection vers ADMIN New HTTP URL = ' + newUrl);
    res.redirect(307, newUrl);
});
// POST 307/303 - Refresh Token
app.post('/api/covid/v1/comptes-utilisateurs/refreshToken', (req, res) => {
    const newUrl = adminSecuriteHost + '/api/covid/v1/comptes-utilisateurs/refreshToken';
    console.log('********* Redirection vers ADMIN New HTTP URL = ' + newUrl);
    res.redirect(307, newUrl);
});
// GET 307/303 - IsAuthenticated
app.get('/api/covid/v1/comptes-utilisateurs/isAuthenticated', (req, res) => {
    const newUrl = adminSecuriteHost + '/api/covid/v1/comptes-utilisateurs/isAuthenticated';
    console.log('********* Redirection vers ADMIN New HTTP URL = ' + newUrl);
    res.redirect(307, newUrl);
});
// POST 307/303 - Mot de passe oublié
app.post('/api/covid/v1/comptes-utilisateurs/mot-de-passe-oublie/**', (req, res) => {
    const urlRequest = req.url;
    console.log('***** URL entrant (Mot de passe oublié) = ' + urlRequest);
    const newUrl = adminSecuriteHost + urlRequest;
    console.log('********* Redirection vers ADMIN New HTTP URL = ' + newUrl);
    res.redirect(307, newUrl);
});
// POST 307/303 - Reset mot de passe
app.post('/api/covid/v1/comptes-utilisateurs/reinitialiser-mot-de-passe', (req, res) => {
    const urlRequest = req.url;
    console.log('***** URL entrant (Reset password) = ' + urlRequest);
    const newUrl = adminSecuriteHost + urlRequest;
    console.log('********* Redirection vers ADMIN New HTTP URL = ' + newUrl);
    res.redirect(307, newUrl);
});
// POST 307/303 - Modifier le Mot de passe
app.post('/api/covid/v1/comptes-utilisateurs/modifier-mot-de-passe', (req, res) => {
    const newUrl = adminSecuriteHost + '/api/covid/v1/comptes-utilisateurs/modifier-mot-de-passe';
    console.log('********* Redirection vers ADMIN New HTTP URL = ' + newUrl);
    res.redirect(307, newUrl);
});
// GET 307/303 - HealthCheck
app.get('/HealthCheck', (req, res) => {
    const newUrl = adminSecuriteHost + '/HealthCheck';
    console.log('********* Redirection vers ADMIN New HTTP URL = ' + newUrl);
    res.redirect(307, newUrl);
});
// GET 307/303 - '.../renvoyer-mail-confirmation-inscription/' + email
// ...
// POST 307/303 - Déconnexion '...deconnexion/' + utilisateurId + '?refreshToken=' + refreshToken
// ...
// GET - Charger le profil
app.get('/api/covid/v1/comptes-utilisateurs/preferences/mon-profil', (req, res) => {
    console.log('****** Redirection vers ADMIN...');
    const newUrl = adminSecuriteHost + '/api/covid/v1/comptes-utilisateurs/preferences/mon-profil';
    console.log('********* New HTTP URL = ' + newUrl);
    res.redirect(newUrl);
});
// PUT 307/303 - Téléverser une photo de profil
app.put('/api/covid/v1/comptes-utilisateurs/preferences/mon-profil', (req, res) => {
    console.log('****** Redirection vers ADMIN...' + req.url);
    const newUrl = adminSecuriteHost + req.url;
    console.log('********* New HTTP URL = ' + newUrl);
    res.redirect(307, newUrl);
});
// POST 307/303 - Téléverser une photo de profil
app.post('/api/covid/v1/comptes-utilisateurs/preferences/televerser-photo', (req, res) => {
    console.log('****** Redirection vers ADMIN...');
    const newUrl = adminSecuriteHost + '/api/covid/v1/comptes-utilisateurs/preferences/televerser-photo';
    console.log('********* New HTTP URL = ' + newUrl);
    res.redirect(307, newUrl);
});
// GET - Télécharger une photo de profil
app.get('/api/covid/v1/comptes-utilisateurs/preferences/telecharger-photo/**', (req, res) => {
    console.log('****** Redirection vers ADMIN...URL=' + req.url);
    const newUrl = adminSecuriteHost + req.url;
    console.log('********* New HTTP URL = ' + newUrl);
    res.redirect(newUrl);
});


// Le service  des données de statistiques
// Statistiques générales
app.get('/api/v1/statistiques-covid/generales', (req, res) => {
    console.log('****** Redirection vers DATA stats...');
    const newUrl = dataStatistiquesHost + '/api/v1/statistiques-covid/generales';
    console.log('********* New HTTP URL = ' + newUrl);
    res.redirect(307, newUrl);
});
// Statistiques sur les régions
app.get('/api/v1/statistiques-covid/regions', (req, res) => {
    console.log('****** Redirection vers DATA stats...');
    const newUrl = dataStatistiquesHost + '/api/v1/statistiques-covid/regions';
    console.log('********* New HTTP URL = ' + newUrl);
    res.redirect(newUrl);
});


// Démarrer le serveur et ecouter sur le port PORT (= 5000)
app.listen(PORT, () => {
    console.log(`Serveur BFF for Covid 19 - FR listenning now on ${PORT}...`);
});

