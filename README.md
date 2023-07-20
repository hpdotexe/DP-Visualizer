# DP-Visualizer

Project is made and testing using JDK 17 - Coretto and hosted using Tomcat 10.1.91 server. 

To access Azure Active Directory properties for authentication (declared inside the Constants class), a ".env" file must be created with all the required tokens inside the classpath (resources folder).

To create Active Directory, a microsoft developer account is needed in which we have to configure an application. After inviting the users and adding them to required groups, add the object id of groups for which we want to give view and edit accesses.

In the GraphqlAPI class where all the graphql endpoints are declared, @RequiresAdminAccess annotation restricts the api to only users with edit access. If not, a ticket is raised and saved in the database.

The DPVisualizer.json file can be imported using retool to access the UI after the backend is deployed.