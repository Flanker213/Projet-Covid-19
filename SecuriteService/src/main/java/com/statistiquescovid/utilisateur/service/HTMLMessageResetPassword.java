package com.statistiquescovid.utilisateur.service;

import com.statistiquescovid.utilisateur.entites.CompteUtilisateur;

public class HTMLMessageResetPassword {

	public static String getEmailContent(String urlReinitialisationPassword, CompteUtilisateur compteUtilisateur, String clientBaseUrl) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<!DOCTYPE html>\r\n" + 
				"<html>\r\n" + 
				"<head>\r\n" + 
				"\r\n" + 
				"  <meta charset=\"utf-8\">\r\n" + 
				"  <meta http-equiv=\"x-ua-compatible\" content=\"ie=edge\">\r\n" + 
				"  <title>Réinitialisation du mot de passe</title>\r\n" + 
				"  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\r\n" + 
				"  <style type=\"text/css\">\r\n" + 
				"  @media screen {\r\n" + 
				"    @font-face {\r\n" + 
				"      font-family: 'Source Sans Pro';\r\n" + 
				"      font-style: normal;\r\n" + 
				"      font-weight: 400;\r\n" + 
				"      src: local('Source Sans Pro Regular'), local('SourceSansPro-Regular'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/ODelI1aHBYDBqgeIAH2zlBM0YzuT7MdOe03otPbuUS0.woff) format('woff');\r\n" + 
				"    }\r\n" + 
				"    @font-face {\r\n" + 
				"      font-family: 'Source Sans Pro';\r\n" + 
				"      font-style: normal;\r\n" + 
				"      font-weight: 700;\r\n" + 
				"      src: local('Source Sans Pro Bold'), local('SourceSansPro-Bold'), url(https://fonts.gstatic.com/s/sourcesanspro/v10/toadOcfmlt9b38dHJxOBGFkQc6VGVFSmCnC_l7QZG60.woff) format('woff');\r\n" + 
				"    }\r\n" + 
				"  }\r\n" + 
				"  body,\r\n" + 
				"  table,\r\n" + 
				"  td,\r\n" + 
				"  a {\r\n" + 
				"    -ms-text-size-adjust: 100%;\r\n" + 
				"    -webkit-text-size-adjust: 100%;\r\n" + 
				"  }\r\n" + 
				"  table,\r\n" + 
				"  td {\r\n" + 
				"    mso-table-rspace: 0pt;\r\n" + 
				"    mso-table-lspace: 0pt;\r\n" + 
				"  }\r\n" + 
				"  img {\r\n" + 
				"    -ms-interpolation-mode: bicubic;\r\n" + 
				"  }\r\n" + 
				"  a[x-apple-data-detectors] {\r\n" + 
				"    font-family: inherit !important;\r\n" + 
				"    font-size: inherit !important;\r\n" + 
				"    font-weight: inherit !important;\r\n" + 
				"    line-height: inherit !important;\r\n" + 
				"    color: inherit !important;\r\n" + 
				"    text-decoration: none !important;\r\n" + 
				"  }\r\n" + 
				"  div[style*=\"margin: 16px 0;\"] {\r\n" + 
				"    margin: 0 !important;\r\n" + 
				"  }\r\n" + 
				"  body {\r\n" + 
				"    width: 100% !important;\r\n" + 
				"    height: 100% !important;\r\n" + 
				"    padding: 0 !important;\r\n" + 
				"    margin: 0 !important;\r\n" + 
				"  }\r\n" + 
				"  table {\r\n" + 
				"    border-collapse: collapse !important;\r\n" + 
				"  }\r\n" + 
				"  a {\r\n" + 
				"    color: #1a82e2;\r\n" + 
				"  }\r\n" + 
				"  img {\r\n" + 
				"    height: auto;\r\n" + 
				"    line-height: 100%;\r\n" + 
				"    text-decoration: none;\r\n" + 
				"    border: 0;\r\n" + 
				"    outline: none;\r\n" + 
				"  }\r\n" + 
				"  </style>\r\n" + 
				"\r\n" + 
				"</head>" +
				"<body style=\"background-color: #e9ecef;\">\r\n" + 
				"  <div class=\"preheader\" style=\"display: none; max-width: 0; max-height: 0; overflow: hidden; font-size: 1px; line-height: 1px; color: #fff; opacity: 0;\">\r\n" + 
				"    Quelqu'un a demander la réinitialisation du mot de passe pour votre compte COVID-19\r\n" + 
				"  </div>\r\n" + 
				"  <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\r\n" + 
				"    <tr>\r\n" + 
				"      <td align=\"center\" bgcolor=\"#e9ecef\">\r\n" + 
				"        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 900px;\">\r\n" + 
				"          <tr>\r\n" + 
				"            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 36px 24px 0; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; border-top: 3px solid #d4dadf;\">\r\n" + 
				"              <h3 style=\"margin: 0; font-size: 20px; font-weight: 700; letter-spacing: -1px; line-height: 48px;\">Bonjour ");
		sb.append(compteUtilisateur.getNom() + ",");
		sb.append("</h3>\r\n" + 
				"            </td>\r\n" + 
				"          </tr>\r\n" + 
				"        </table>\r\n" + 
				"      </td>\r\n" + 
				"    </tr>\r\n" + 
				"    <tr>\r\n" + 
				"      <td align=\"center\" bgcolor=\"#e9ecef\">\r\n" + 
				"        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 900px;\">\r\n" + 
				"          <tr>\r\n" + 
				"            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\r\n" + 
				"              <p style=\"margin: 0;\">Quelqu'un a demandé un nouveau mot de passe pour votre compte <a href=\"" + clientBaseUrl + "\" target=\"_blank\"><b>Covid 19 - FR</b></a>. \r\n" + 
				"			  Si c'est vous qui êtes à l'origine de cette opération, veuillez cliquer le bouton ci-dessous pour réinitialiser le mot de passe. </p>\r\n" + 
				"            </td>\r\n" + 
				"          </tr>\r\n" + 
				"          <tr>\r\n" + 
				"            <td align=\"left\" bgcolor=\"#ffffff\">\r\n" + 
				"              <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\">\r\n" + 
				"                <tr>\r\n" + 
				"                  <td align=\"center\" bgcolor=\"#ffffff\" style=\"padding: 12px;\">\r\n" + 
				"                    <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\">\r\n" + 
				"                      <tr>\r\n" + 
				"                        <td align=\"center\" bgcolor=\"#1a82e2\" style=\"border-radius: 6px;\">\r\n" + 
				"                          <a href=\"");
		sb.append(urlReinitialisationPassword + "\" target=\"_blank\" style=\"display: inline-block; padding: 16px 36px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; color: #ffffff; text-decoration: none; border-radius: 6px;\">Réinitialisez votre Mot de passe</a>\r\n" + 
				"                        </td>\r\n" + 
				"                      </tr>\r\n" + 
				"                    </table>\r\n" + 
				"                  </td>\r\n" + 
				"                </tr>\r\n" + 
				"              </table>\r\n" + 
				"            </td>\r\n" + 
				"          </tr>\r\n" + 
				"          <tr>\r\n" + 
				"            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\r\n" + 
				"              <p style=\"margin: 0;\">Si cela ne fonctionne pas, copiez et collez le lien suivant dans votre navigateur:</p>\r\n" + 
				"              <p style=\"margin: 0;\"><a href=\"");
		sb.append(urlReinitialisationPassword + "\" target=\"_blank\">");
		sb.append(urlReinitialisationPassword + "</a></p>\r\n" + 
				"            </td>\r\n" + 
				"          </tr>\r\n" + 
				"		  <tr>\r\n" + 
				"            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px;\">\r\n" + 
				"              <p style=\"margin: 0;\">Si vous n'avez pas demandé de nouveau mot de passe, \r\n" + 
				"			  vous pouvez ignorer cet email en toute sécurité, ou répondre pour nous faire savoir. Cette réitinialisation sera valide seulement dans les prochaines 30 minutes.</p>\r\n" + 
				"            </td>\r\n" + 
				"          </tr>\r\n" + 
				"          <tr>\r\n" + 
				"            <td align=\"left\" bgcolor=\"#ffffff\" style=\"padding: 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 16px; line-height: 24px; border-bottom: 3px solid #d4dadf\">\r\n" + 
				"              <p style=\"margin: 0;\">Bien cordialement,<br> L'Equipe Covid 19 - FR</p>\r\n" + 
				"            </td>\r\n" + 
				"          </tr>\r\n" + 
				"        </table>\r\n" + 
				"      </td>\r\n" + 
				"    </tr>\r\n" + 
				"    <tr>\r\n" + 
				"      <td align=\"center\" bgcolor=\"#e9ecef\" style=\"padding: 24px;\">\r\n" + 
				"        <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" style=\"max-width: 900px;\">\r\n" + 
				"          <tr>\r\n" + 
				"            <td align=\"center\" bgcolor=\"#e9ecef\" style=\"padding: 12px 24px; font-family: 'Source Sans Pro', Helvetica, Arial, sans-serif; font-size: 14px; line-height: 20px; color: #666;\">\r\n" + 
				"              <p style=\"margin: 0;\">Copyright 2022 - Covid 19 - FR, Tout droit resérvé</p>\r\n" + 
				"              <p style=\"margin: 0;\">93200 Ile-de-France, Paris France</p>\r\n" + 
				"            </td>\r\n" + 
				"          </tr>\r\n" + 
				"        </table>\r\n" + 
				"      </td>\r\n" + 
				"    </tr>\r\n" + 
				"  </table>\r\n" + 
				"</body>\r\n" + 
				"</html>");
		
		return sb.toString();
	}
}
