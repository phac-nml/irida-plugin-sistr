# IRIDA SISTR Plugin for Salmonella serotyping
This new IRIDA plugin allows deployment of SISTR, a tool for in silico Salmonella serotyping, in IRIDA platform. For faster and independent updates, SISTR built-in workflow were decided to be ported to plugin format. Plugin version has a limitation using the most current IRIDA version `20.05.2`- inability to generate analysis report page. Future releases of IRIDA should bring this feature to plugins. In future SISTR releases we plan to generate reports in `csv` and `pdf` formats allowing for easy download after completion of the analysis run.



# Features
New to version 1.1.1 incorporates

* Updated typing databases
* More detailed reports for MASH, cgMLST and BLAST profiles

# Building plugin
Compiled plugin version is available in `jar` folder, but you can build your own copy. Building and packaging this code is accomplished using [Apache Maven](http://maven.apache.org/download.cgi). However, you will first need to install [IRIDA](https://github.com/phac-nml/irida) to your local Maven repository. The version of IRIDA you install will have to correspond to the version found in the `irida.version.compiletime` property in the `pom.xml` file of this project. To build successfully plugin there is a need to compile IRIDA corresponding to the version specified in `pom.xml`. 
Here is a brief workflow to compile new `*.jar` file from the source code 

```bash
# Build IRIDA dependencies
git clone https://github.com/phac-nml/irida.git
git checkout 19.01.3
#IRIDA dependencies will be located in ~/.m2
mvn install -DskipTests 
git clone https://github.com/phac-nml/irida-plugin-sistr.git
cd irida-plugin-sistr
# Build ECTyper plugin
mvn package -DskipTests  #find your package in /target
# move to *.jar to /etc/irida/plugins/
```
Below you will find more detailed explanations of each step above.

# Install
As most IRIDA plugins, this plugin is readily installable by the placement of the `*.jar` file (from the Releases section of this repo) in `/etc/irida/plugins` directory. After IRIDA server restart, the new pipeline should appear in the list of pipelines. 


# Dependencies

The following dependencies are required to make and run this plugin.

* IRIDA >= 19.01.3
* Java >= 1.8 and Maven >= 3.3.9 (to build IRIDA dependencies)
* Galaxy >= 16.01
* Shovill == 1.1.0
* SISTR == 1.1.1

# Galaxy configuration
The plugin assumes a properly configured Galaxy instance that will run the workflow included in the plugin.
For this version of the plugin, the backend Galaxy instance needs to have the following tools installed via the `Admin` interface.

* Shovill v1.1.0
	* version 1.1.0
  	* revision 6:83ead2be47b2
  	* published 2020-07-03
  	* [ToolShed direct link](https://toolshed.g2.bx.psu.edu/repos/iuc/shovill/shovill/1.1.0+galaxy0)

  
* SISTR v1.1.1
	* version 1.1.1
	* revision 4:17fcac7ddf54
	* published 2020-08-06
	* [ToolShed direct link](https://toolshed.g2.bx.psu.edu/view/nml/sistr_cmd/17fcac7ddf54)

# Gallery
A couple of illustrations demonstrating plugin in action.

# Feature updates
* inclusion of the following new fields in the IRIDA Project `Line List` output
	* `cgmlst_found_loci`
	* `serovar_antigen`
	* `mash_serovar`
	* `mash_subspecies`
	* `cgmlst_genome_match`
	* `mash_genome`
	* `mash_distance`
	* `qc_messages`
* update to the newest `shovill` version `1.1.0` with more relaxed assembly setting with minimum contig length of 1bp


### Automatic metadata population
The plugin allows for automatic IRIDA project metadata population and automatic triggering upon sequencing data upload. These features need to be updated in the IRIDA project configuration page.

<p align="center">
  <img src="./pics/PipelineTile.png">
</p>
<p align="center" style="font-style:bold;font-size: 20px">Figure 1: Pipeline Tile</p>

<p align="center">
  <img src="./pics/LineListMeta.png">
</p>
<p align="center" style="font-style:bold;font-size: 20px">Figure 2: Key metadata fields populated after serotyping run</p>
<p align="center"></p>


### Troubleshooting
After successful installation, plugin should appear in the list of available pipelines. If not, check web-server log files for errors (e.g. Tomcat 7 logs `/var/log/tomcat7/catalina.out`)


### Testing
Folder `sampledata` has
* stripped versions of the `Salmonella Newport` raw reads from `[SRR12168692](https://www.ncbi.nlm.nih.gov/sra/SRR12168692)` accession. These files could be used to test plugin in your IRIDA instance
* `SISTR_results.json` example results file output by the tool 





