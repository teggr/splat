# splat

Simple platform-as-a-service for Spring Boot deployments

# build instructions

    mvn clean install

Server jar available in target/splat-web.jar

# deploy server jar to Digital Ocean

1. log into digital ocean
2. create a new project
3. skip moving resources
4. get started with a droplet
5. distribution: choose an ubuntu distribution
6. size: standard 1gb/1CPU
7. datacenter: London (optional add DO monitoring agent)
8. Create a new SSH key or use existing
9. 1 droplet 
10. create droplet
11. go to droplet configuration
12. create ssh session using private key + ip address + root username (mobaxterm)
13. follow digital ocean initial server setup docs (https://www.digitalocean.com/community/tutorials/initial-server-setup-with-ubuntu-16-04)
    1. keep hold of your password and ssh key
14. install java (default-jdk) (https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-ubuntu-16-04)
15. make directory ~/splat-runtime
16. upload the splat-web.jar to this directory
17. make directory ~/splat-runtime/config
18. Create a application.properties file in the config directory. See below for details.
19. Create a SystemD service. See below for details.
20. Start the service running.
21. use a tunnel to access the web application (mobaxterm/ssh)

# set configuration

create a spring configuration file

	cd ~/splat-runtime
	mkdir config
	nano config/application.properties

	# splat platform	
	# splat.home-directory= uncomment to change. defaults to ${user.home}/.splat
	
	# access
	access.username=<a username>
	access.password=<a strong password> # see https://passwordsgenerator.net/

# monitor init.d / SystemV service

See https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-systemd-service

Create the systemd service script

	sudo vi /etc/systemd/system/splat-web.service

	[Unit]
	Description=splat-web
	After=syslog.target
	
	[Service]
	User=splat
	ExecStart=/home/splat/splat-runtime/splat-web.jar
	SuccessExitStatus=143
	
	[Install]
	WantedBy=multi-user.target

	sudo service splat-web [start|restart|stop]
	
	sudo journalctl -u splat
	
# upload application

	go to index.html
	log in
	upload the a spring boot jar
