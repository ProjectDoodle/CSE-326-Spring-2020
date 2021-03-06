##  When first getting the Dockerfile run: 
    "docker build -t rubycustom ." 
in a directory with the Dockerfile\
    **NOTE:** do not rename the file or this will not work\
    **NOTE:** This process may take awhile, this is normal

## After the image is created run:

    "docker run --name rubytest -p 127.0.0.2:3000:3000 -dit rubycustom bash"

 - --name names the container
 - -p 127.0.0.2:3000:3000 maps the 127.0.0.2:3000 to port 3000 in the container
   - **NOTE:** 3000 is the default port rails listens on
- -dit starts the container detached, starts an interactive session, simulates tty

## Reusing Container

To access the container you use:

    "docker attach rubytest"

If you close the container you must use: 

    "docker start rubytest" followed by "docker attach rubytest"

## Inside the container

When in the container you can modify the files in '/Ephemeral' to modify the rails environment

When testing you can use:

    "rails server -b 0.0.0.0"
To start the server