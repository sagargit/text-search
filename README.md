## Running the Application
The application provides a **Text Search Console** to search text inputs from files.

`TextSearchApp.scala` is the entrypoint to the application. While running this file, path to a directory containing text 
files must be passed as command line arguments. The program then looks inside all the files contained in the directory
and processed them to serve search requests.

This project has `test_directory` folder with some test files. So, it can be used as directory path while running the
application.

Thus, the application can be started using `runMain` as follows:

```
sbt "runMain com.rock.search.TextSearchApp test_directory"
```

OR it also can be started using `run`:

```
sbt "run test_directory"
```

The above commands will start the Text Search Console which will look like below:

```
Welcome to Search Console.
Enter `:quit` to exit the Console anytime.
Please enter your search input.

search>
```

Then the user will have to enter search string. For eg:

```
search>average person
```

Then the application will respond with top 10 list of matching files ranked based on relevance. 
For eg:

```
File => 203.txt. Match Score => 100.0 %
File => vol04.iss0064-0118.txt. Match Score => 100.0 %
File => vol09.iss0050-0100.txt. Match Score => 100.0 %
File => vol08.iss0001-0071.txt. Match Score => 100.0 %
File => codegeek.txt. Match Score => 100.0 %
File => ethics.txt. Match Score => 95.0 %
File => tr823.txt. Match Score => 95.0 %
File => adventur.txt. Match Score => 95.0 %
File => time.txt. Match Score => 95.0 %
File => howtobbs.txt. Match Score => 90.0 %
```

To quit the Search Console, the user has to input `:quit` as follows:
```
search>:quit
```

## Running Tests

Unit tests can be executed as follows:
```
sbt test
```

Integration Test can be executed as follows:
```
sbt it:test
```

All tests can be executed together as:
```
sbt test it:test
```
