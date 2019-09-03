UNBABEL Backend Engineering Challenge
=====================================

-> Introduction:

The class "Unbabelcli" was written in Java and process a file with translation stats.
It receives the input file name and the window size in minutes as runtime arguments.
It reads the text file in JSON format and create a sorted map in memory, with the duration time for every minute already summed, for better performance and memory saving.
After that, it creates an output file in JSON format, with the moving average time, for every minute.

-> Compilation:

For compilation execute: ./comp.sh

-> Execution:

Syntax: ./unbabel_cli --input_file <json file pathname> --window_size <num. minutes>


-> Notes:
	* Uses "org.json.jar" library
	* Written using Java 1.6.0_33
	