# splat

Simple platform-as-a-service for Spring Boot deployments

# build instructions

    mvn clean install

Server jar available in target/splat-web.jar

# deploy server jar to Digital Ocean

## create digital ocean droplet

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
    
## install java

1. install java (default-jdk) (https://www.digitalocean.com/community/tutorials/how-to-install-java-with-apt-get-on-ubuntu-16-04)

## install splat application as a service

1. make directory ~/splat-runtime
2. upload the splat-web.jar to this directory
3. make directory ~/splat-runtime/config
4. Create a application.properties file in the config directory. See below for details.
5. Create a SystemD service. See below for details.
6. Start the service running.

## install nginx as a reverse proxy (alternative to tunnel)

1. follow instructions on https://www.digitalocean.com/community/tutorials/how-to-install-nginx-on-ubuntu-18-04
2. should be able to access the nginx home page using droplet ip address http://{droplet.ipaddress}/
3. backup then open default site
	sudo cp /etc/nginx/sites-enabled/default ~/nginx.default.bkp
	sudo vi /etc/nginx/sites-enabled/default
4. set the location entry
	      location / {
                proxy_set_header        Host $host;
                proxy_set_header        X-Real-IP $remote_addr;
                proxy_set_header        X-Forwarded-For $proxy_add_x_forwarded_for;
                proxy_set_header        X-Forwarded-Proto $scheme;

                proxy_pass              http://localhost:8080;
                proxy_read_timeout      90;

                proxy_redirect          http://localhost:8080   http://$host;
                
                client_max_body_size	1000M;
                
        }
5. restart nginx
	sudo service nginx restart

## access application through SSH tunnel (alternative to nginx)

1. use a tunnel to access the web application (mobaxterm/ssh)

# set configuration

create a spring configuration file

	cd ~/splat-runtime
	mkdir config
	nano config/application.properties
	
application properties

	# splat platform	
	# splat.home-directory=${user.home}/.splat
	# splat.os.ports.from-inclusive=9091
	# splat.os.ports.from-inclusive=10091
	
	# access
	splat.web.access.username=<a username>
	splat.web.access.password=<a strong password> # see https://passwordsgenerator.net/
	
	# logging
	logging.path=/home/splat/.splat/logs

# monitor init.d / SystemV service

See https://docs.spring.io/spring-boot/docs/current/reference/html/deployment-install.html#deployment-systemd-service

Create the systemd service script

	sudo vi /etc/systemd/system/splat-web.service

	[Unit]
	Description=splat-web
	After=syslog.target
	
	[Service]
	User=splat	
	Environment="JAVA_OPTS=-Djava.security.egd=file:/dev/./urandom"	
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
