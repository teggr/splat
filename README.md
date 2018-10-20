# splat

Simple platform-as-a-service for Spring Boot deployments

# build instructions

    mvn clean install

Server jar available in target/splat-web-{version}.jar

# deploy server jar to Digital Ocean

1. log into digital ocean
2. create a new project
3. skip moving resources
4. get started with a droplet
5. distribution: choose an ubuntu distribution
6. size: standard 1gb/1CPU
7. datacenter: London
8. Create a new SSH key or use existing
9. 1 droplet
10. create droplet
11. go to droplet configuration
12. create ssh session using private key + ip address + root username (mobaxterm)
13. follow digital ocean initial server setup docs (https://www.digitalocean.com/community/tutorials/initial-server-setup-with-ubuntu-16-04)
    1. keep hold of your password and ssh key
14. install java (default-jdk) (https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-ubuntu-16-04)
15. make directory ~/splat-runtime
16. upload the jar to this directory
17. run the jar - java -jar splat-web-{version}.jar (see configuration section first!!)
18. use a tunnel to access the web application (mobaxterm/ssh)

# set configuration

create a spring configuration file

	cd ~/splat-runtime
	mkdir config
	nano config/application.properties

	# storage
	storage.path=/tmp/splat/storage
	
	# access
	access.username=<a username>
	access.password=<a strong password> # see https://passwordsgenerator.net/

# upload application

	go to index.html
	upload the file. note will clear out with each restart
