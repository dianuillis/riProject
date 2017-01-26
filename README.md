### Projet RI
#### Team
Alhassane Bah  
Diana Ramirez  
Hamza Baaziz  
Yassin Bendari
#### Requirements
It's a JAVA shell commands application that **requires JDK 7 & MAVEN 3**
#### How to parameter the application ?
**{root} = root folder of the project**

| Class  | Variable | Description
| ----- | ----- | -----
| ujm.dsc.ri.output.Outputer | OUTPUT_FOLDER_PATH | relative (to {root}) path of the folder where run files will be generated
| ujm.dsc.ri.output.Outputer | TEAM_NAME | name of the Team (for run name)
| ujm.dsc.ri.parse.QueryParser | QUERY_PATH | relative path of queries file
| ujm.dsc.ri.parse.TextParser | DOC_TEXT_PATH | relative path of the collection text file
| ujm.dsc.ri.parse.XmlParser | XML_FOLDER_PATH | relative path of the folder of the collection xml files
| **ujm.dsc.ri.shell.RISCommands** | **RUNS_PARAMS_FILE** | **relative path of the file that describes runs to produce**
| ujm.dsc.ri.plot.Ploter | EVALUATION_FOLDER_PATH | relative path of the folder of runs evaluations
| ujm.dsc.ri.plot.Ploter | STATS_FOLDER_PATH | relative path where to generate plots for runs evaluations
| ujm.dsc.ri.plot.Ploter | CSV_OUTPUT_FILE | relative path of the comparison CSV file of runs evaluations
| ujm.dsc.ri.plot.Ploter | WIDTH | width of plot (jpg)
| ujm.dsc.ri.plot.Ploter | HEIGHT | height of plot (jpg)

#### How to parameter the runs ?
In the RUNS_PARAMS_FILE (${root}/input/run.txt) :  
Describe the runs that you want to generate respecting the same format given in the examples inside **(it's self explanatory)**

#### How to build ?
```
{root} > $ mvn clean package
```
  
#### How to run ?
```
{root} > $ java -jar target/RIS-1.0-BETA.jar
```
#### Available commands
**run**  
produces runs in ${root}/output/, according to what's defined in ${root}/input/runs.txt  
**plot**  
plot method charts in ${root}/stats/, runs evaluation must before be placed in ${root}/input/evaluation/
#### Notes
Project can be imported in eclipse: File>Import>Maven>Existing Maven project
#### M2 DSC 2016/17