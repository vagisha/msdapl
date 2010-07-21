



<!--
- Copyright (c) 2006 The European Bioinformatics Institute, and others.
- All rights reserved. Please see the file LICENSE
- in the root directory of this distribution.
-
- PRIDE spectrum viewing JSP
-
- @author Phil Jones
- @version $Id: viewSpectrum.jsp,v 1.17 2006/10/30 11:54:10 philip_jones Exp $
-->





<html>
<head>
    <title>
        PRIDE Spectrum Viewer.
        Experiment Accession: 3763
		Spectrum Reference:	380
    </title>
    <script type="text/javascript" src="/msdapl/js/pride/wz_jsgraphics.js"></script>

	<script type="text/javascript" src="/msdapl/js/pride/AminoAcidClass.js"></script>
	<style type="text/css">
        td,legend {
		font-family: verdana, arial, sans-serif;
		font-size: 11;
		font-weight: bold;
		}

        input {
		font-family: verdana, arial, sans-serif;
		font-size: 11;
		font-weight: normal;
		border: none;
		}
	</style>
</head>

<body style="margin:0px;border:0px;padding:0px;">
<!-- The following script import must remain in the body section.-->
<script type="text/javascript" src="/msdapl/js/pride/wz_dragdrop.js"></script>
<table border="0" width="100%" cellpadding="0" cellspacing="0">
	<tr>
		<td width="*">

			<img hspace="0" id = "headerBar" style="margin:0px;border:0px;padding:0px;z-index:5;" src="/msdapl/images/pride/spectrumViewer/repeat_vertical_line.gif" alt="" width="100%" height="100" align = "left"/>
		</td>
		<td width="83">
			<img hspace="0" style="margin:0px;border:0px;padding:0px;z-index:5;" src="/msdapl/images/pride/spectrumViewer/logo_ebi_pride.gif" alt="" width="83" height="100" align = "right"/>
		</td>
	</tr>
</table>
<img name="printer" style="position:absolute;left:10px;top:34px;display:block;z-index:10;" src="/msdapl/images/pride/spectrumViewer/fileprint.gif" width="32" height="32" alt="Print Spectrogram" title="Print Spectrogram"/>
<img name="grid" style="position:absolute;left:46px;top:34px;display:block;z-index:10;" src="/msdapl/images/pride/spectrumViewer/gridOff.gif" width="32" height="32" alt="Toggle Grid Lines" title="Toggle Grid Lines"/>
<img name="annotation" style="position:absolute;left:82px;top:34px;display:block;z-index:10;" src="/msdapl/images/pride/spectrumViewer/annotationOn.gif" width="32" height="32" alt="Toggle Annotation" title="Toggle Annotation"/>
<img name="peakListHeading" style="position:absolute;left:118px;top:12px;display:block;z-index:10;" src="/msdapl/images/pride/spectrumViewer/peak_list.gif" alt="" title="" width="66" height="16"/>

<img name="valuesText" style="position:absolute;left:118px;top:34px;display:block;z-index:10;" src="/msdapl/images/pride/spectrumViewer/valuesTEXT.gif" width="32" height="32" alt="View m/z and intensity values (Plain Text)" title="Retrive m/z and intensity value (Plain Text)"/>
<img name="valuesHTML" style="position:absolute;left:154px;top:34px;display:block;z-index:10;" src="/msdapl/images/pride/spectrumViewer/valuesHTML.gif" width="32" height="32" alt="View m/z and intensity values (HTML table)" title="View m/z and intensity values (HTML table)"/>
<img name="zoomOut" style="position:absolute;left:190px;top:34px;display:block;z-index:10;" src="/msdapl/images/pride/spectrumViewer/viewmag1.gif" width="32" height="32" alt="Reset Zoom" title="Reset Zoom"/>
<img name="track" style="position:absolute;left:250px;top:18px;display:block;z-index:10;" src="/msdapl/images/pride/spectrumViewer/track.gif" width="100" height="50" alt="Zoom Tool" title="Zoom Tool"/>
<img name="leftBound" src="/msdapl/images/pride/spectrumViewer/uparrow.gif" width="20" height="20" alt="Left m/z Zoom" title="Left m/z Zoom"/>
<img name="rightBound" src="/msdapl/images/pride/spectrumViewer/uparrow.gif" width="20" height="20" alt="Right m/z Zoom" title="Right m/z Zoom"/>
<img name="panZoomWindow" src="/msdapl/images/pride/spectrumViewer/small_blue.gif" width="105" height="4" alt="Pan Zoom Window" title="Pan Zoom Window"/>
<img name="intensityBound" src="/msdapl/images/pride/spectrumViewer/rightarrow.gif" width="20" height="20" alt="Intensity Zoom" title="Intensity Zoom"/>
<img name="massErrorTrack" style="position:absolute;left:400px;top:44px;display:block;z-index:10;" src="/msdapl/images/pride/spectrumViewer/mass_error_track.gif" width="130" height="31" alt="Modify the mass error (Daltons)" title="Modify the mass error (Daltons)"/>
<img name="massPointer" src="/msdapl/images/pride/spectrumViewer/uparrow.gif" width="20" height="20" alt="Modify the mass error (Daltons)" title="Modify the mass error (Daltons)"/>
<img name="denovoText" style="position:absolute;left:716px;top:34px;display:block;z-index:10;" src="/msdapl/images/pride/spectrumViewer/valuesTEXT.gif" width="32" height="32" alt="View de novo sequence (Plain Text)" title="View de novo sequence (Plain Text)"/>
<img name="denovoHTML" style="position:absolute;left:752px;top:34px;display:block;z-index:10;" src="/msdapl/images/pride/spectrumViewer/valuesHTML.gif" width="32" height="32" alt="View de novo sequence (HTML table)" title="View de novo sequence (HTML table)"/>

    <img name="resetSelectedPeaks" style="position:absolute;left:680px;top:34px;display:block;z-index:10;" src="/msdapl/images/pride/spectrumViewer/undo.gif" width="32" height="32" alt="Remove all peak selections" title="Remove all peak selections"/>
    <img name="denovoHeading" style="position:absolute;left:716px;top:12px;display:block;z-index:10;" src="/msdapl/images/pride/spectrumViewer/de_novo.gif" alt="" title="" width="66" height="16"/>

<img name="help" style="position:absolute;left:890px;top:34px;display:block;z-index:10;" src="/msdapl/images/pride/spectrumViewer/help.gif" width="32" height="32" alt="Help" title="Help"/>
<div style="position:absolute;top:100px;left:0px;">
    <table align="center" width="100%">
        <tr>
            <td align="right" valign="top">
                <table>
                    <tr>
                        <td align="left">
                            <span style="color:#999999;display:block;font-weight:bold;font-style:normal;" id="mzKey">

                                m/z
                                <br/>
                                <input type="text" id="mzDisplay" value="" size="8" readonly="readonly"/>
                            </span>
                            <span style="color:#999999;display:block;font-weight:normal;font-style:italic;" id="intenKey">
                                Intensity
                                <br/>
                                <input type="text" id="intenDisplay" value="" size="8" readonly="readonly"/>
                            </span>
                        </td>

                    </tr>
                </table>
            </td>
        </tr>
        <tr>
            <td>
                <div id="denovoDisplay"></div>
            </td>
        </tr>

    </table>
</div>

<div id="errorD" style="position:absolute;display:block;left:375px;top:7px;z-index:10;">
    <table>
        <tr>
            <td colspan="2" align="center">
                Mass Error
                <input type="text" id="errorDVal" value="" size="8"
                       style="border:thin;border-style:solid;" readonly="readonly"></input>
                Da
            </td>

        </tr>
    </table>
</div>

<div id="chargeState" style="position:absolute;display:block;left:570px;top:25px;z-index:10;">
	 <table>
		 <tr>
			 <td align = "center">
				 Include ions<br/>
				 where z &gt; 1
			 </td>

		</tr>
		<tr>
			 <td align = "center" valign="middle">
				 <input type = "checkbox" name="chargeCheck" onchange="setCharge(this);" onclick="setCharge(this);"/>
			 </td>
		 </tr>
	 </table>
</div>

<div id="yOrB" style="position:absolute;display:block;left:788px;top:20px;width:80px;z-index:10;">

    <form name="ionSeriesForm">
		<table align="center" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td colspan="2" align="center">Ion Series</td>
			</tr>
			<tr>
				<td align="right" style="color:red;">
					Y
				</td>

				<td align = "center">
					<input type = "radio" name="ionSeries" value="y" checked="true" onchange="seriesChanged();" onclick="seriesChanged();"></input>
				</td>
			</tr>
			<tr>
				<td align="right" style="color:blue;">
					B
				</td>
				<td align = "center">

					<input type = "radio" name="ionSeries" value="b" onchange="seriesChanged();" onclick="seriesChanged();"></input>
				</td>
			</tr>
		</table>
    </form>
</div>


<div id="spectrogramCanvas">
</div>

<div id="peakInfoDiv"
     style="position:absolute;left:100px;top:100px;width:130px;padding:6px;border:1px solid #000099;background:#d6e6ff;font-family:verdana,arial,sans-serif;font-size:11;font-weight:normal;display:none;"></div>

<script type="text/javascript" language="JavaScript1.1">
<!--
/*******************************************************************\
Script to render a zoomable mass spectrogram
============================================

Phil Jones, EMBL-EBI, September 2005.
Version 2.0 May 2006 - Now includes simple de novo peptide sequencing
inspired by an interface developed by Lennart Martens.

This script makes use of the Javascript Vector Graphics Library
from www.walterzorn.com that can be found at:
http://www.walterzorn.com/jsgraphics/jsgraphics_e.htm

and the drag & drop DHTML library that can be found at:
http://www.walterzorn.com/dragdrop/dragdrop_e.htm

Tested successfully on:
Firefox all versions (Linux, Apple OSX & Windows),
Internet Explorer 6 (Windows and Apple OSX).
Konqueror 3.4.2 (Linux)
Opera (Windows and Apple OSX)

Note: to use dynamically, just need to re-write the two arrays
holding mz and intensity values and the title
of the graph. (e.g. in a JSP).

LICENSE: LGPL

This script is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License (LGPL) as published by the Free Software Foundation; either
version 2.1 of the License, or (at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

For more details on the GNU Lesser General Public License,
see http://www.gnu.org/copyleft/lesser.html
\*******************************************************************/

/********************************************************\
		Variables that need to be written dynamically
\********************************************************/

// var mz = new Array (120.154,126.123,127.904,129.04,130.064,131.088,132.073,133.136,134.066,135.082,136.102,140.165,141.18,142.069,143.165,145.172,146.055,148.029,149.094,150.061,151.215,152.109,152.911,154.25,155.179,156.322,157.142,158.081,159.324,161.242,163.41,164.035,165.398,166.139,167.03,169.158,169.986,171.241,172.02,173.187,173.986,175.152,176.253,179.064,181.018,182.107,183.158,183.97,185.145,186.097,187.228,188.201,189.222,190.126,191.229,192.261,193.217,193.999,195.132,196.218,197.258,198.209,199.214,200.173,201.022,203.169,204.484,205.24,206.176,207.201,208.228,209.242,210.304,211.159,212.377,213.272,215.378,216.166,217.012,218.406,219.277,220.097,221.171,222.208,223.097,224.243,225.288,226.337,227.223,228.104,229.185,230.218,231.277,232.222,233.175,233.877,235.314,236.181,237.078,238.722,239.412,240.343,241.014,242.156,243.133,244.017,245.24,246.236,247.292,248.171,249.003,250.315,251.217,252.307,253.072,254.291,254.982,256.208,257.33,258.199,258.979,261.14,262.141,263.293,264.186,265.133,266.227,267.142,268.099,269.05,270.248,271.466,272.18,273.626,274.302,276.143,277.168,278.267,279.359,280.295,281.203,282.047,283.342,285.224,286.106,287.061,288.453,290.406,292.197,293.187,294.246,295.203,296.206,297.138,298.324,299.572,300.283,301.22,302.1,303.987,305.282,306.466,307.371,308.126,309.574,311.425,312.245,313.238,314.325,315.37,316.469,317.222,318.138,319.136,319.861,321.607,322.424,323.451,324.575,325.347,326.367,327.304,328.31,329.386,330.354,331.272,332.383,333.365,334.221,335.096,336.229,337.236,338.56,339.315,340.59,341.43,342.357,343.358,344.273,345.555,346.244,347.04,347.823,349.035,350.44,351.261,352.119,353.148,354.103,355.4,356.389,357.398,358.209,359.317,361.253,362.015,363.08,364.229,365.012,365.792,366.47,367.281,368.212,369.41,370.377,371.95,373.044,374.184,375.129,376.566,377.667,378.556,379.171,380.092,381.195,382.124,383.096,384.36,385.513,386.448,388.215,389.456,390.192,391.371,392.427,393.394,394.374,395.35,396.952,398.091,398.928,400.3,401.297,402.235,403.234,403.862,404.82,408.879,419.475,422.526,424.082,436.333,437.359,438.287,439.481,440.259,441.645,443.251,444.287,445.161,446.127,446.854,448.356,450.358,451.311,451.988,452.991,454.3,455.587,458.398,460.303,461.371,464.132,465.188,466.129,467.425,468.686,469.662,470.45,471.397,472.817,473.586,474.709,476.088,477.003,480.283,481.497,482.257,483.378,484.317,485.247,486.305,487.252,488.501,489.743,491.141,492.163,493.529,498.386,499.403,500.349,501.348,502.278,503.181,504.087,505.35,506.381,507.307,508.075,510.262,511.375,512.329,514.612,515.439,516.444,517.347,518.256,519.384,520.975,522.363,523.683,524.35,525.778,527.673,528.339,529.379,530.302,531.693,532.371,534.496,535.135,536.233,537.034,537.755,538.374,539.33,540.289,541.345,542.301,543.376,544.454,546.155,547.353,548.368,549.821,551.145,552.277,553.452,554.366,555.4,556.366,557.118,558.242,558.868,559.567,560.485,561.142,562.205,563.362,565.172,566.264,568.317,569.224,570.466,571.441,572.541,573.479,575.33,577.35,578.458,579.187,580.27,581.395,582.389,583.302,584.312,585.437,586.238,587.272,589.308,591.578,592.81,594.247,595.22,596.17,598.392,599.321,600.481,601.387,602.65,603.337,604.042,605.366,606.313,607.322,608.032,609.526,610.66,611.308,612.369,613.312,614.421,615.404,616.25,617.365,618.385,619.408,620.348,621.224,622.301,623.253,624.267,625.179,626.434,627.67,628.442,629.724,630.475,631.346,632.685,634.402,635.568,636.509,637.325,638.847,639.593,640.483,641.336,642.306,644.308,645.266,648.367,650.222,652.533,653.216,654.829,655.73,656.511,657.44,658.346,659.36,660.443,661.271,663.041,666.405,667.75,668.501,669.434,670.192,671.411,672.515,673.544,675.238,676.266,677.392,678.296,679.463,680.671,681.45,682.342,683.055,684.753,685.479,686.703,688.391,689.403,690.306,691.239,692.276,694.17,695.359,696.357,697.263,698.534,699.953,700.609,701.658,702.549,704.449,707.126,708.283,709.178,711.346,712.41,713.491,714.341,722.446,723.462,725.326,726.396,727.399,728.258,729.247,730.474,732.376,733.355,737.507,738.228,739.307,740.249,741.296,742.335,743.268,744.371,745.478,746.629,751.156,752.473,753.635,754.386,755.546,756.28,757.386,759.596,765.199,767.58,768.414,769.265,770.414,771.401,781.258,783.054,787.889,792.137,818.358,819.476,822.603,859.717,865.752);
// var inten = new Array (335.0,83.01,65.58,7290.0,10870.0,1088.0,456.8,1478.0,335.7,338.5,1096.0,225.0,422.0,305.8,208.5,138.3,620.6,69350.0,2566.0,138.8,35.45,226.4,263.1,185.9,653.7,189.2,834.7,176.5,234.0,89.6,240.0,54.65,940.9,13670.0,591.3,1174.0,270.2,1136.0,32.59,999.9,286.9,561.0,51.34,230.2,515.3,1391.0,879.5,226.4,534.9,450.2,1739.0,596.8,410.3,214.5,637.7,143.7,131.4,271.7,381.4,204.9,1267.0,3831.0,1043.0,855.0,702.4,925.8,314.8,490.2,139.8,145.2,211.4,631.9,158.9,412.8,425.3,90.82,1593.0,12080.0,323.3,231.0,123.7,309.4,309.9,337.8,621.0,745.2,773.6,3094.0,2424.0,596.1,1802.0,303.0,1342.0,29.13,70.17,51.21,344.6,422.0,603.5,91.08,745.5,852.1,927.0,4228.0,3205.0,1043.0,1349.0,1138.0,2786.0,572.2,372.1,80.84,390.9,124.0,1117.0,418.0,83.45,339.5,418.6,1158.0,1085.0,42130.0,3000.0,89.28,95.62,339.9,278.8,663.6,1807.0,74.74,889.5,178.4,1165.0,227.9,206.6,2526.0,2904.0,931.6,712.2,417.3,899.8,859.1,899.4,594.8,1462.0,300.9,1359.0,663.6,174.8,415.9,939.5,4709.0,2179.0,496.7,519.7,1447.0,940.6,111.2,1150.0,6873.0,1927.0,1086.0,480.2,534.0,83.77,930.6,135.8,5474.0,1219.0,1509.0,768.5,489.5,312.6,1132.0,984.7,1015.0,575.2,491.8,978.7,539.4,315.7,1373.0,344.2,2322.0,816.0,1871.0,477.3,796.3,948.9,32.51,633.3,1456.0,2781.0,585.7,3773.0,2864.0,9746.0,1878.0,353.4,114.3,123.8,607.0,3257.0,650.9,1752.0,1249.0,1249.0,360.8,957.0,6313.0,14220.0,4208.0,3808.0,2554.0,1417.0,584.9,1607.0,3077.0,1936.0,274.8,158.2,1058.0,368.6,2359.0,3333.0,8920.0,3203.0,11290.0,2601.0,4042.0,4190.0,336.5,717.5,1560.0,622.9,496.3,1033.0,1863.0,3480.0,1840.0,1849.0,4820.0,185.9,4096.0,1695.0,928.7,2542.0,522.2,2745.0,4686.0,8674.0,793.7,1614.0,1118.0,3277.0,875.5,341.1,103.4,136.7,164.8,460.3,2113.0,180.5,213.6,406.8,200.7,428.2,791.1,497.2,151.1,378.5,261.3,48.19,102.0,181.5,85.18,525.5,196.9,983.9,228.3,265.1,125.4,99.99,426.9,223.5,1455.0,1121.0,1351.0,1531.0,698.7,309.7,79.24,155.4,315.7,111.0,301.2,137.5,218.5,388.2,882.5,21120.0,3827.0,5868.0,900.9,443.2,38.67,609.4,120.8,1229.0,1324.0,456.0,1745.0,689.6,299.1,1393.0,1745.0,1538.0,280.7,109.2,253.2,572.8,75.86,875.0,1265.0,2358.0,1466.0,560.9,345.1,35.53,92.33,221.3,999.6,756.1,678.6,2571.0,4565.0,532.5,412.9,54.62,105.9,267.7,4017.0,3030.0,1169.0,767.0,211.9,73.7,612.0,1263.0,1542.0,668.0,5791.0,2239.0,334.2,265.1,1743.0,1139.0,726.0,311.8,20900.0,5348.0,588.2,483.8,146.7,314.7,362.8,153.1,86.19,392.0,5117.0,676.2,168.3,529.8,473.5,376.6,772.8,495.1,60.75,324.8,326.3,127.1,1301.0,2444.0,462.8,2229.0,2645.0,724.8,801.4,429.2,850.6,215.8,42.38,697.2,540.3,159.5,83910.0,19170.0,754.0,106.1,471.8,313.2,189.6,360.0,420.9,74.17,73.79,867.9,374.5,471.4,10780.0,3693.0,1839.0,1339.0,1037.0,5069.0,4207.0,1945.0,2242.0,1065.0,146.5,166.1,262.7,373.1,285.9,638.0,859.4,717.5,304.0,624.5,312.2,284.2,56.51,723.8,2591.0,1192.0,549.7,410.1,898.2,958.6,44180.0,5594.0,87.16,216.4,86.42,678.6,248.6,405.2,2157.0,7187.0,1291.0,3420.0,749.9,32.56,73.66,184.9,1304.0,723.1,616.7,581.5,844.9,366.6,396.9,1610.0,3954.0,811.3,649.8,123.1,422.8,70.06,731.5,72.21,216.9,1012.0,483.1,88.45,37.13,152.7,364.3,970.4,47660.0,4854.0,619.4,73.83,152.2,535.5,59.05,236.7,208.3,146.1,156.9,59.2,1082.0,184300.0,56030.0,4746.0,271.5,26.26,274.1,9805.0,4668.0,5626.0,6537.0,888.1,321.8,331.8,83.19,241.0,335.1,2741.0,1513.0,679.4,2561.0,3945.0,1434.0,1092.0,378.1,246.9,372.7,192.7,117.5,38.67,256.1,475.7,146.1,256.6,307.9,108.6,209.4,3329.0,782.6,175.9,248.4,227.6,122.7,35.69,236.2,73.92,105.1,325.5);
var mz = new Array (155.341,157.133,159.052,170.199,171.115,175.067,177.139,178.136,182.172,183.171,184.149,185.177,186.129,187.047,188.115,189.024,194.184,199.327,200.108,201.216,203.215,205.157,211.162,215.068,217.253,218.14,221.261,225.102,228.197,229.324,233.25,237.123,245.041,246.212,248.181,254.247,260.352,262.207,267.338,271.334,272.263,273.262,274.23,275.23,276.668,281.341,284.28,285.239,286.256,287.245,288.239,289.188,293.225,294.227,295.493,296.319,297.028,299.656,302.298,303.317,307.456,308.22,312.296,313.23,315.16,316.276,319.281,320.35,326.144,327.33,328.402,330.298,333.151,335.429,337.383,341.349,343.296,344.496,345.271,347.725,349.303,350.33,351.2,352.349,353.39,355.319,356.165,357.984,360.528,361.333,362.288,363.989,365.028,367.252,368.442,369.873,370.754,371.366,373.429,374.286,375.437,377.861,378.905,381.171,382.266,383.697,384.602,385.233,387.363,389.303,392.847,394.165,395.353,396.292,397.249,400.391,401.909,403.569,404.413,405.435,406.346,407.208,408.278,409.341,410.852,411.926,413.271,414.286,415.219,417.374,421.711,423.263,426.462,427.488,428.432,429.474,432.368,434.268,435.376,436.367,438.201,440.261,440.927,442.253,443.248,444.322,445.394,448.259,449.595,450.487,451.544,452.199,454.534,455.292,456.763,458.257,459.323,460.494,461.252,462.542,466.581,467.313,468.617,470.935,471.975,473.736,474.626,475.387,476.459,479.546,480.346,481.281,482.252,483.321,485.53,486.634,488.736,490.019,492.326,493.287,494.521,496.088,496.795,497.435,498.198,499.338,500.309,501.388,502.03,502.915,504.176,504.822,506.219,507.252,508.255,509.174,509.885,510.7,511.506,512.867,513.853,514.57,515.913,516.931,519.178,535.218,537.321,538.254,539.983,541.319,542.318,543.471,545.324,547.675,548.42,549.449,551.319,553.425,554.653,556.132,557.299,558.11,561.616,562.395,563.41,564.293,569.484,570.515,574.237,575.385,578.154,579.331,580.557,581.333,582.453,583.289,584.213,587.477,588.303,590.185,590.803,592.225,597.368,598.349,599.397,600.352,601.144,602.278,602.995,607.128,608.591,609.497,610.412,613.648,616.548,617.463,618.47,619.293,620.462,621.779,622.418,624.973,625.767,626.458,628.197,628.984,630.521,631.476,634.25,635.361,636.481,637.355,638.403,639.421,640.286,641.41,642.336,645.373,646.385,648.359,649.505,650.371,652.482,655.431,656.518,657.524,658.617,659.403,660.511,663.382,665.055,665.894,666.611,668.431,669.664,670.412,672.38,674.11,675.039,676.234,677.486,678.752,680.093,681.568,682.542,684.555,686.107,688.224,689.173,690.479,694.602,695.425,696.353,699.495,700.455,703.015,704.411,705.425,706.423,707.265,713.453,714.505,715.47,716.52,719.75,720.985,722.376,725.35,726.374,727.395,729.932,730.778,731.907,733.167,736.582,737.522,738.542,739.337,741.469,742.689,745.567,748.416,754.587,755.604,756.445,757.309,763.528,764.539,765.9,767.662,768.737,770.527,775.581,783.15,785.608,786.426,790.453,791.57,795.711,798.558,802.497,803.354,804.366,809.702,814.567,820.371,821.42,822.435,823.748,825.228,828.537,829.583,830.377,831.512,835.421,838.25,839.413,841.515,843.579,847.509,849.818,851.567,853.673,854.418,855.576,856.602,864.355,865.513,867.563,869.541,870.459,871.262,873.474,874.709,875.573,877.589,879.592,881.617,902.955,903.719,907.436,914.73,915.478,918.595,919.619,921.628,922.963,923.598,936.495,938.584,940.506,952.671,976.725,991.659,1010.645,1020.52,1028.077,1032.662,1037.629,1055.995,1056.884,1059.502,1063.792);
var inten = new Array (151.23,75.64,71.44,93.37,667.87,667.95,147.48,521.64,87.46,343.37,162.79,480.29,71.12,442.79,118.41,126.12,149.83,428.3,3046.32,547.56,107.24,97.1,336.56,151.56,314.94,2080.24,102.34,340.71,6061.99,353.1,295.27,113.48,60.96,435.44,176.67,220.75,54.64,70.76,259.1,174.94,1310.38,447.87,209.2,376.69,166.5,57.66,819.16,220.6,336.51,192.93,368.32,243.08,109.63,750.67,91.3,103.74,64.46,164.31,825.2,274.61,308.71,161.1,348.71,516.49,548.91,273.24,2483.39,141.53,206.27,415.16,603.04,54.23,322.3,141.41,64.15,565.92,449.92,1524.44,697.42,324.07,170.13,137.55,203.16,409.63,2056.49,586.13,172.17,119.59,730.82,791.91,65.37,818.73,188.96,468.56,784.56,15313.7,273.54,1450.67,1431.72,1695.35,700.93,13326.23,1593.96,52.83,1287.51,491.22,246.89,289.58,266.37,110.03,470.6,843.81,1021.66,138.64,107.32,453.06,530.54,1179.32,586.69,813.17,471.27,142.79,457.19,1685.03,8038.4,403.02,1210.67,550.34,506.54,65.1,344.64,360.79,910.06,817.99,2065.48,1474.56,792.35,722.74,1753.88,228.17,60.64,814.16,439.73,760.49,437.65,786.67,158.0,4391.23,2117.46,127.08,879.48,71.03,2035.04,54.42,1562.02,2742.56,2064.79,2135.76,1424.74,2257.51,291.42,668.71,1350.38,10341.39,461.07,247.57,251.57,253.29,1052.98,1411.12,2359.51,1204.75,1160.07,240.98,184.46,330.11,5302.71,2006.94,152.45,661.63,186.34,1884.94,1002.09,2342.23,565.65,1538.53,144.65,2130.25,597.37,361.21,770.3,797.83,588.49,2745.13,2365.33,2131.99,1403.74,139.05,484.41,4231.08,3181.99,781.03,3591.15,4761.49,3016.12,165.87,59.19,1411.47,742.56,597.01,661.75,520.06,363.34,613.59,214.89,741.58,207.35,781.47,333.59,472.59,366.05,214.1,858.71,105.7,669.65,2749.39,490.65,426.91,1566.27,208.71,1201.33,187.19,6614.75,1554.31,491.63,220.55,251.61,1397.26,497.65,780.45,353.57,246.59,728.79,12223.98,1647.36,1961.41,1171.29,968.39,596.57,954.92,2255.73,167.39,201.67,519.29,1773.34,2171.25,1485.49,908.15,1555.03,858.24,124.69,2160.49,619.39,366.73,3363.99,390.25,402.59,614.09,17394.65,5616.1,2314.89,786.63,7259.4,2372.79,291.61,3594.17,436.89,116.9,137.27,118.69,200.98,321.13,307.01,5697.99,2885.55,977.2,494.05,842.16,364.92,624.4,9555.77,2825.33,310.38,413.44,597.93,85.75,241.9,21045.48,9422.46,1162.42,1088.99,718.49,392.58,714.94,298.12,1200.14,166.77,665.12,75.6,565.38,480.06,1511.11,913.46,86.43,380.31,529.42,290.01,8312.57,3780.57,503.69,1041.95,1483.84,307.06,226.3,835.38,572.91,266.02,296.11,321.32,470.71,759.88,1357.68,185.28,397.19,645.31,3962.67,680.78,180.13,588.37,335.38,159.64,138.3,3111.54,1584.85,75.61,950.46,775.27,864.53,1328.97,252.18,104.93,83.07,99.53,55.33,52.46,143.41,120.77,240.77,138.2,175.73,835.06,616.68,894.94,285.35,1311.27,13508.42,6350.18,1323.03,227.01,108.34,423.71,471.15,148.92,225.27,482.06,98.83,304.9,268.37,308.14,195.81,358.47,138.77,176.38,296.73,169.32,343.83,193.63,208.95,99.88,3129.69,1051.75,609.17,66.47,514.61,284.6,214.98,239.63,180.61,132.49,1067.36,130.63,316.15,167.48,272.86,650.25,70.25,199.62,467.86,605.99,266.66,263.23,217.38,182.59,92.18,168.23,176.78,133.66,569.15,216.88,442.49,678.8,274.85,58.45);
var typeArray = null;
var labelArray = null;
var colourArray = null;
var additionalArray = null;
var graphTitle = "Sequence:  	K.NILSAVGVDADAETAK.L";



/****************************************************\
		DEFINE GLOBAL VARIABLES
\****************************************************/

// Array to hold selected peaks for de novo sequencing
// Indexed by a String being the mz value of the peak.
var selectedPeaksArray = new Array(2);

selectedPeaksArray[0] = new Array(); // Y ion series
selectedPeaksArray[1] = new Array(); // B ion series
// Zoom tool dimensions (MUST be the same as the track width / height above.)
var TRACK_WIDTH = 100;
var TRACK_HEIGHT = 50;

var deNovoAllowed = false;

var MAX_INTENSITY_FACTOR = 10000;

// Set up variables for zooming (Set to no zoom).
var leftMzZoom = 0;
var rightMzZoom = TRACK_WIDTH;
var intensityZoom = MAX_INTENSITY_FACTOR;

// Variables to hold current window size
var winW = 0;
var winH = 0;

// Constants to hold size and number of axis ticks
var TICK_LENGTH=5;
var TICK_NUMBER=10;

// Variables to hold current mouse position.
var mouseX = 0;
var mouseY = 0;

// Reference to most recently selected peak.
var lastSelectedPeak = new Array(2);
lastSelectedPeak[0]=null;   // Y ion series
lastSelectedPeak[1]=null;   // B ion series

var currentSelectedPeakColour = new Array(2);
currentSelectedPeakColour[0]="ff0000";
currentSelectedPeakColour[1]="0000ff";

var notCurrentSelectedPeakColour = new Array(2);
//notCurrentSelectedPeakColour[0]="#FF9999";
notCurrentSelectedPeakColour[0]="#ffbbbb";
notCurrentSelectedPeakColour[1]="#bbbbff";

var currentSeries = null;

var currentSeriesMessageHtml = new Array(2);
currentSeriesMessageHtml[0] = "<br/><span style='color:red;'>Y Series</span>";
currentSeriesMessageHtml[1] = "<br/><span style='color:blue;'>B Series</span>";

var hasSelectedPeak = false;

// milliseconds timeout for peak details to disappear.
var PEAK_DETAILS_TIMEOUT = 4000;

// Variables holding names of all the relevant images.
var LEFT_MZ_BOUND = "leftBound";
var RIGHT_MZ_BOUND = "rightBound";
var INTENSITY_BOUND = "intensityBound";
var ZOOM_OUT = "zoomOut";
var TRACK = "track";
var PRINTER = "printer";
var GRID = "grid";
var ANNOTATION = "annotation";
var VALUES_TEXT = "valuesText";
var VALUES_HTML = "valuesHTML";
var PEAK_INFO_DIV = "peakInfoDiv";
var MASS_ERROR_TRACK = "massErrorTrack";
var MASS_ERROR_POINTER = "massPointer";
var MASS_ERROR_DISPLAY_DIV_ID = "errorD";
var MASS_ERROR_DISPLAY_ID = "errorDVal"
var RESET_SELECTED_PEAKS = "resetSelectedPeaks";
var HELP = "help";
var DENOVO_TEXT = "denovoText";
var DENOVO_HTML = "denovoHTML";
var DENOVO_HEADING = "denovoHeading";
var PEAKLIST_HEADING = "peakListHeading";
var HEADER_BAR = "headerBar";
var PAN_ZOOM_WINDOW = "panZoomWindow";
// Correction for the distance of the arrow pointer from top left of the image.
var POINTER_CORRECTION = -11;

var MASS_ERROR_TRACK_CORRECTION = 15;

var ARROW_BASE_WIDTH_CORRECTION = -3;

var MINIMUM_HEADING_WIDTH = 200;
var MINIMUM_RIGHT_SIDE_WIDTH = 100;

var SELECTED_PEAK_ARROW_ELEVATION = new Array(2);
SELECTED_PEAK_ARROW_ELEVATION[0] = 68;    // Elevation for Y ion annotation
SELECTED_PEAK_ARROW_ELEVATION[1] = 23;    // Elevation for B ion annotation

// Variable indicating default state of grid.
var grid = false;

// Variable indicating if peaks should be annotated.
var annotate = true;

// Graphics object for drawing spectrum.
var graphicsCanvas = new jsGraphics("spectrogramCanvas");

// Find the smallest and largest values in the two arrays.
var minMz = smallest (mz);
var maxMz = largest (mz);
var maxInten = largest (inten);

var HEADING_FONT_SIZE = "18px";
var LABEL_FONT_SIZE = "13px";
var NUMBER_FONT_SIZE = "11px";

// Slim these fonts down a bit for IE.
if (dd.ie){
	HEADING_FONT_SIZE = "16px";
	LABEL_FONT_SIZE = "11px";
	NUMBER_FONT_SIZE = "9px";
}

/****************************************************\
		Functions to make it all work...
\****************************************************/

// Define class holding x and y coordinates of top of displayed peak and the corresponding m/z value.
function DisplayedPeak (mz, intensity, x, y1, y2, arrayIndex){
	this.mz = mz;
	this.intensity = intensity;
	this.x = x;
	this.y1 = y1;
	this.y2 = y2;
    this.arrayIndex = arrayIndex;
}

// Define array that will hold all of these DisplayedPeak objects with names of form '345' where 345 is the
// x coordinate of the plot and the value is a DisplayedPeak object.
var mzToPeakHash;

// Function to return the largest value in an array of numbers.
function largest (myArray){
	var biggest = null;
	for (var counter = 0; counter < myArray.length; counter++){
		if (biggest == null || myArray[counter] > biggest){
			biggest = myArray[counter];
		}
	}
	return biggest;
}

// Function to return the smallest value in an array of numbers.
function smallest (myArray){
	var smallest = null;
	for (var counter = 0; counter < myArray.length; counter++){
		if (smallest == null || myArray[counter] < smallest){
			smallest = myArray[counter];
		}
	}
	return smallest;
}

/*
  Computes the mass error in the logarithmic range 0.0001 -> 10 from
  a scale value (from the GUI widget) in the range 0 -> 100.
*/
function scaleToMassError (relativeScale){
    return Math.pow(10, (relativeScale/20) -4);
}

/*
  Computes the scale value (from the GUI widget) in the range 0 -> 100
   from the mass error in the logarithmic range 0.0001 -> 10.
*/
function massErrorToScale (massError){
    return Math.round(( (Math.log (massError) / Math.LN10) + 4) * 20);
}

//Calculate the initial relative position of the mass error pointer
var massErrorXPos = massErrorToScale (Molecule.MASS_ERROR_DALTONS);

/*
   Sets global variables to window width / height.
*/
function getWindowSize(){
	winW = dd.getWndW();
	winH = dd.getWndH();
}

// Called to draw / redraw the spectrum based on current zoom / window size.
// param printable - boolean - draw in printable form (slower).
// param completeDraw - when zooming, a partial (fast) draw is performed
// to make the zooming smoother.
function drawSpectrogram(printable, completeDraw){
	// Two arrays holding mz and intensity values.
	if (printable != null){
		 graphicsCanvas.setPrintable(printable);
	}
	else {
		 graphicsCanvas.setPrintable(false);
	}

	getWindowSize();

	// Define boundaries of chart (Nominally set to be 80% of the available width / height)
	var left = Math.round(winW * 0.1);
	var right = winW - MINIMUM_RIGHT_SIDE_WIDTH;
	if (right - left < 50) right = left + 50;
	var top = MINIMUM_HEADING_WIDTH;
	var bottom = Math.round(winH * 0.9);

    var lastDrawnSelectedPeak = new Array(2);
    lastDrawnSelectedPeak[0]=null; // Y series
    lastDrawnSelectedPeak[1]=null; // B series

	// Get drawing...
	graphicsCanvas.clear();
	graphicsCanvas.setColor("#888888");
	// Draw y axis
	graphicsCanvas.drawLine(left, top, left, bottom + TICK_LENGTH);
	// Draw x axis
	graphicsCanvas.drawLine(left - TICK_LENGTH, bottom, right, bottom);

	// Only tick the axes if this is a complete draw (do
	// not do this while zooming for speed.)
    if (completeDraw){
        if (grid){
            graphicsCanvas.drawLine(right, bottom, right, top);
        }
        // Tick y axis
        for (ytick=top; ytick <= bottom; ytick+=(bottom - top) / TICK_NUMBER){
            graphicsCanvas.drawLine (left-TICK_LENGTH ,Math.round(ytick), (grid) ? right : left , Math.round(ytick));
        }
        // Tick x axis
        for (xtick=left; xtick <=right; xtick+=(right-left) / TICK_NUMBER){
            graphicsCanvas.drawLine (Math.round(xtick), (grid) ? top : bottom, Math.round(xtick), bottom+TICK_LENGTH);
        }
	}

	// This is just to ensure there are no nasty index out of bounds errors, caused by the sizes of the mz / intensity arrays being different.
	lineCount = Math.min (mz.length, inten.length);

	graphicsCanvas.setColor("#000000");

	// Calculate the zoom limits.
	var minMzLimit = (( maxMz - minMz) * (leftMzZoom / TRACK_WIDTH)) + minMz;
	var maxMzLimit = (( maxMz - minMz) * (rightMzZoom / TRACK_WIDTH)) + minMz;
	var intensityFactor =  maxInten * (intensityZoom / 10000);

	// Define an array used for finding the top n intensities.
	var topIntens = new Array();

	// Reset the peak array for the new draw.
	mzToPeakHash = new Array();

    var lastPeakXCoordinate = -1;
    var topPeakYCoordinate = bottom;
    var lastPeakColour = null;
    // Draw the peaks themselves.
	for (var indexer = 0; indexer < lineCount; indexer++){
		// Only draw them if the fall within the bounds of minMzlimit and maxMzLimit (zoom boundaries)
		if (completeDraw || inten[indexer]/intensityFactor > 0.03){

		if (mz[indexer] >= minMzLimit && mz[indexer] <= maxMzLimit){
			y1 = Math.round( Math.max( top, (top + (((intensityFactor - inten[indexer])/intensityFactor) * (bottom - top)))));
			x = Math.round( left + (((mz[indexer] - minMzLimit) / (maxMzLimit - minMzLimit)) * (right - left)) );

            // Set the selected peaks to the Y and B ions passed in as annotation.
            if (typeArray != null){
                if (typeArray[indexer] == "Y"){
                    selectedPeaksArray[0]["" + (mz[indexer])] = new DisplayedPeak (mz[indexer], inten[indexer], x, y1, bottom, indexer);
                }
                else if (typeArray[indexer] == "B"){
                    selectedPeaksArray[1]["" + (mz[indexer])] = new DisplayedPeak (mz[indexer], inten[indexer], x, y1, bottom, indexer);
                }
            }

            if (colourArray != null){
                col = colourArray[indexer];
            }
            // Optimisation to ensure only one line is drawn on each pixel column.
			if (lastPeakXCoordinate == -1){
			    lastPeakXCoordinate = x;
			    topPeakYCoordinate = bottom;
                if (colourArray != null){
                    lastPeakColour = col;
                }
            }
			else if (x != lastPeakXCoordinate){
				// OK, moved on from the last X coordinate, so draw the (previous) maximum line.
				// .. but only if there is actually something to draw...
				if (topPeakYCoordinate < bottom){
                    if (colourArray != null){
                        var switchBackColour = graphicsCanvas.color;
                        graphicsCanvas.setColor(lastPeakColour);
                        graphicsCanvas.drawLine (lastPeakXCoordinate, topPeakYCoordinate, lastPeakXCoordinate, bottom);
                        graphicsCanvas.setColor(switchBackColour);
                    }
                    else {
                        graphicsCanvas.drawLine (lastPeakXCoordinate, topPeakYCoordinate, lastPeakXCoordinate, bottom);
                    }
                }
			    lastPeakXCoordinate = x;
			    topPeakYCoordinate = y1;
                if (colourArray != null){
                    lastPeakColour = col;
                }
            }
            else {
            	if (y1 < topPeakYCoordinate){
            	    topPeakYCoordinate = y1;
				}
			}

			if (completeDraw){

				// Store details of peak in Object hash.
				var peakKey = "" + x;
				existingEntry = mzToPeakHash[peakKey];
				currentPeak = new DisplayedPeak (mz[indexer], inten[indexer], x, y1, bottom, indexer);

				// Only add a peak to the array if it is the largest so far at that point.
				if (existingEntry == null || existingEntry.y1 > y1){
					// Ok - not recorded this x coordinate yet, or have but for a lower intensity peak.
					mzToPeakHash[peakKey] = currentPeak;
				}
                for (seriesIndex = 0; seriesIndex <= 1; seriesIndex++){
                    // Update the selectedPeaksArray for both series with the latest x, y1, bottom values.)
                    updateSelectedPeak = selectedPeaksArray[seriesIndex]["" + (mz[indexer])];
                    if (updateSelectedPeak != null){
                        updateSelectedPeak.x = currentPeak.x;
                        updateSelectedPeak.y1 = currentPeak.y1;
                        updateSelectedPeak.bottom = currentPeak.bottom;
                    }

                    selectionLineTopY = top - SELECTED_PEAK_ARROW_ELEVATION[seriesIndex];
                    // Draw red line above selected peak;
                    peak = selectedPeaksArray[seriesIndex]["" + (mz[indexer])];
                    if (peak != null){
                        // This is a selected peak, so draw in the additional lines.
                        if (lastSelectedPeak[seriesIndex] != null && peak.mz == lastSelectedPeak[seriesIndex].mz){
                            graphicsCanvas.setColor(currentSelectedPeakColour[seriesIndex]);
                            graphicsCanvas.setStroke(2);
                        }
                        else {
                            graphicsCanvas.setColor(notCurrentSelectedPeakColour[seriesIndex]);
                            graphicsCanvas.setStroke(1);
                        }
                        graphicsCanvas.drawLine (x, y1, x, selectionLineTopY);

                        graphicsCanvas.setColor(notCurrentSelectedPeakColour[seriesIndex]);
                        graphicsCanvas.setStroke(1);
                        // If a previous peak was drawn, draw a connecting line and
                        // annotate it.
                        if (lastDrawnSelectedPeak[seriesIndex] != null){
                            graphicsCanvas.drawLine (lastDrawnSelectedPeak[seriesIndex].x, selectionLineTopY, x, selectionLineTopY);
                            graphicsCanvas.setStroke(1);
                            // Draw arrows, size dependent on distance between lines.
                            xDist = Math.abs (lastDrawnSelectedPeak[seriesIndex].x - x);

                            // Determine arrow head size.
                            var arrowSize = (xDist > 50) ?  5 :
                                            (xDist > 40) ?  4 :
                                            (xDist > 30) ?  3 :
                                            (xDist > 20) ?  2 :
                                                            0 ;
                            // Draw the arrow heads if the line is long enough.
                            if (arrowSize > 0){
                                var correction = 1;
                                if (lastDrawnSelectedPeak[seriesIndex].x - x < 0){
                                    correction = -1;
                                }

                                graphicsCanvas.drawLine (x, selectionLineTopY, x + (correction * arrowSize), selectionLineTopY + arrowSize);
                                graphicsCanvas.drawLine (x, selectionLineTopY, x + (correction * arrowSize), selectionLineTopY - arrowSize);
                                graphicsCanvas.drawLine (lastDrawnSelectedPeak[seriesIndex].x, selectionLineTopY, lastDrawnSelectedPeak[seriesIndex].x - (correction * arrowSize), selectionLineTopY + arrowSize);
                                graphicsCanvas.drawLine (lastDrawnSelectedPeak[seriesIndex].x, selectionLineTopY, lastDrawnSelectedPeak[seriesIndex].x - (correction * arrowSize), selectionLineTopY - arrowSize);
                            }

                            // Write on the details of the mass difference.
                            graphicsCanvas.setColor("#000000");
                            graphicsCanvas.setFont("verdana",NUMBER_FONT_SIZE,Font.BOLD);
                            // Calculate the mass difference
                            mzDelta = Math.abs (lastDrawnSelectedPeak[seriesIndex].mz - mz[indexer]);
                            var type = (seriesIndex == 0) ? "Y":"B";
                            graphicsCanvas.drawStringRect (createMatchHTML (mzDelta, type), (lastDrawnSelectedPeak[seriesIndex].x > x) ? x : lastDrawnSelectedPeak[seriesIndex].x , selectionLineTopY + 2, xDist, "left");

                        }
                        graphicsCanvas.setColor("#000000");
                        graphicsCanvas.setStroke(1);
                        lastDrawnSelectedPeak[seriesIndex] = peak;
                    }
				}
			}
			if (annotate && completeDraw){
				// Add the current intensity to the bottom of the topEleven array and sort (desc)
				topIntens[topIntens.length] = inten[indexer];
			}
		}
		}
	}
	// Draw the last peak.
	if (topPeakYCoordinate < bottom){
        if (colourArray != null){
            switchBackColour = graphicsCanvas.color;
            graphicsCanvas.setColor(lastPeakColour);
            graphicsCanvas.drawLine (lastPeakXCoordinate, topPeakYCoordinate, lastPeakXCoordinate, bottom);
            graphicsCanvas.setColor(switchBackColour);
        }
        else{
            graphicsCanvas.drawLine (lastPeakXCoordinate, topPeakYCoordinate, lastPeakXCoordinate, bottom);
        }
    }
	// Write heading
	var headingWidth = Math.round((Math.max (200, winW / 2)));
	graphicsCanvas.setFont("arial",HEADING_FONT_SIZE,Font.BOLD);
	graphicsCanvas.drawStringRect (graphTitle, 0, 105, winW, "center");

	if (completeDraw){
		// Write some labels
		graphicsCanvas.setFont("verdana",LABEL_FONT_SIZE,Font.BOLD);
		graphicsCanvas.drawStringRect ("Intensity", 2, top - 26, 100, "left");
		graphicsCanvas.drawStringRect ("m/z", Math.round((winW - headingWidth)/2), Math.round((winH-bottom)/2 + bottom), headingWidth,  "center");

		// Add on max and min for each axis.
		graphicsCanvas.setFont("verdana",NUMBER_FONT_SIZE,Font.PLAIN);

		// Work out a sensible frequency of tick values depending on height / width of the chart
		var horizTickFreq = (right - left > 600) ?  1 :
							(right - left > 400) ?  2 :
							(right - left > 200) ?  5 :
											10;

		var verticTickFreq = (bottom - top > 300) ? 1 :
							 (bottom - top > 150) ? 2 :
							 (bottom - top > 100) ? 5 :
											10;

		// Tick y axis value
		for (ytick=top; ytick <= bottom; ytick+=((bottom - top) / TICK_NUMBER) * verticTickFreq){
			// Calculate the yTickValue and round it appropriately.
			yTickValue = intensityFactor * (bottom - ytick) / (bottom - top);
			if (intensityFactor < 10){
			    yTickValue = "" + yTickValue.toFixed(3);
            }
            else {
                yTickValue = "" + Math.round(yTickValue);
            }
			graphicsCanvas.drawStringRect (yTickValue, 0, ytick - 7, left - (TICK_LENGTH + 2), "right");
		}
		// Tick x axis value
		for (xtick=left; xtick <=right; xtick+=(right-left) / TICK_NUMBER * horizTickFreq){
			xTickValue = "" + Math.round (minMzLimit + ((maxMzLimit - minMzLimit) * (xtick - left) / (right - left)));
			graphicsCanvas.drawStringRect (xTickValue, xtick - 40, bottom + TICK_LENGTH + 2, 80, "center");
		}

		if (annotate){
			var annotationCount=(right - left > 700) ?  15 :
								(right - left > 400) ?  10 :
								(right - left > 200) ?  5 :
												3;

			topIntens.sort(function(a,b){return b-a;});  //Sort descending
			minIntensityToLabel = topIntens[annotationCount - 1];
			graphicsCanvas.setColor("#999999");
			// Annotate the peaks that have intensities above the threshold.
			for (indexer = 0; indexer < lineCount; indexer++){
                // Add the annotation if the intensity is above the threshold, or the peak has external annotation (e.g. ion type)
                if (mz[indexer] >= minMzLimit && mz[indexer] <= maxMzLimit &&
					((minIntensityToLabel == null || inten[indexer] >= minIntensityToLabel) ||
                    (labelArray != null && labelArray[indexer] != "-"))){
					y = Math.round( Math.max( top + 35, (top + (((intensityFactor - inten[indexer]) / intensityFactor) * (bottom - top)))));
					x = Math.round( left + (((mz[indexer] - minMzLimit) / (maxMzLimit - minMzLimit)) * (right - left)) );
                    if (labelArray != null && labelArray[indexer] != "-"){
                        // Have an external annotation, so display the ion label
                        // Colour is dependent on ion type.
                        graphicsCanvas.setColor(colourArray[indexer]);
                        graphicsCanvas.drawStringRect (labelArray[indexer], x + 2 , y-24, 50, "left");
                        graphicsCanvas.setColor("#999999");
                    }
                    graphicsCanvas.setFont("verdana",NUMBER_FONT_SIZE,Font.BOLD);
					graphicsCanvas.drawStringRect (Math.round(mz[indexer]), x + 2, y-12, 50, "left");
					graphicsCanvas.setFont("verdana",NUMBER_FONT_SIZE,Font.ITALIC);
					graphicsCanvas.drawStringRect (Math.round(inten[indexer]), x + 2, y, 50, "left");
                }
			}
		}
	}
	graphicsCanvas.paint();
}

// Place the three arrows in the correct starting (unzoomed) positions relative to the track gif.
function resetArrowPositions(){
	dd.elements.leftBound.moveTo(dd.elements.track.x + POINTER_CORRECTION, dd.elements.track.y + TRACK_HEIGHT - 1);
	dd.elements.rightBound.moveTo(dd.elements.track.x + TRACK_WIDTH + POINTER_CORRECTION, dd.elements.track.y + TRACK_HEIGHT - 1);
	dd.elements.intensityBound.moveTo(dd.elements.track.x - 20, dd.elements.track.y + POINTER_CORRECTION);
	// Position the pan tool.
	dd.elements.panZoomWindow.moveTo(dd.elements.track.x + ARROW_BASE_WIDTH_CORRECTION, dd.elements.track.y + TRACK_HEIGHT + 20);
	// Change the width of the pan tool
	dd.elements.panZoomWindow.resizeTo(dd.elements.rightBound.x - dd.elements.leftBound.x + 5, 4);
	dd.elements.panZoomWindow.hide();
}

/*
  Moves all of the clickable widgets to their starting positions, sets up their
  moveable status.
*/
function initialiseWidgets(){
	FIXED_CLICKABLE = CURSOR_HAND + HORIZONTAL + MAXOFFLEFT + 0 + MAXOFFRIGHT + 0;

    if (deNovoAllowed){
        SET_DHTML(LEFT_MZ_BOUND + CURSOR_HAND + HORIZONTAL + MAXOFFLEFT + 0 + MAXOFFRIGHT + TRACK_WIDTH,
              MASS_ERROR_POINTER + CURSOR_HAND + HORIZONTAL + MAXOFFLEFT + 0 + MAXOFFRIGHT + TRACK_WIDTH,
              RIGHT_MZ_BOUND + CURSOR_HAND + HORIZONTAL + MAXOFFLEFT + TRACK_WIDTH + MAXOFFRIGHT + 0,
              PAN_ZOOM_WINDOW + CURSOR_HAND + HORIZONTAL + MAXOFFLEFT + 0 + MAXOFFRIGHT + 0,
              INTENSITY_BOUND + CURSOR_HAND + VERTICAL + MAXOFFBOTTOM + TRACK_HEIGHT + MAXOFFTOP + 0,
              ZOOM_OUT + FIXED_CLICKABLE,
              PRINTER + FIXED_CLICKABLE,
              GRID + FIXED_CLICKABLE,
              ANNOTATION + FIXED_CLICKABLE,
              VALUES_TEXT + FIXED_CLICKABLE,
              VALUES_HTML + FIXED_CLICKABLE,
              TRACK + FIXED_CLICKABLE,
              PEAK_INFO_DIV,
              MASS_ERROR_TRACK + FIXED_CLICKABLE,
              RESET_SELECTED_PEAKS + FIXED_CLICKABLE,
              HELP + FIXED_CLICKABLE,
              DENOVO_TEXT + FIXED_CLICKABLE,
              DENOVO_HTML + FIXED_CLICKABLE,
              DENOVO_HEADING + FIXED_CLICKABLE,
              PEAKLIST_HEADING + FIXED_CLICKABLE,
              MASS_ERROR_DISPLAY_DIV_ID + FIXED_CLICKABLE);
    }
    else {
        SET_DHTML(LEFT_MZ_BOUND + CURSOR_HAND + HORIZONTAL + MAXOFFLEFT + 0 + MAXOFFRIGHT + TRACK_WIDTH,
			  MASS_ERROR_POINTER + CURSOR_HAND + HORIZONTAL + MAXOFFLEFT + 0 + MAXOFFRIGHT + TRACK_WIDTH,
			  RIGHT_MZ_BOUND + CURSOR_HAND + HORIZONTAL + MAXOFFLEFT + TRACK_WIDTH + MAXOFFRIGHT + 0,
			  PAN_ZOOM_WINDOW + CURSOR_HAND + HORIZONTAL + MAXOFFLEFT + 0 + MAXOFFRIGHT + 0,
			  INTENSITY_BOUND + CURSOR_HAND + VERTICAL + MAXOFFBOTTOM + TRACK_HEIGHT + MAXOFFTOP + 0,
			  ZOOM_OUT + FIXED_CLICKABLE,
			  PRINTER + FIXED_CLICKABLE,
			  GRID + FIXED_CLICKABLE,
			  ANNOTATION + FIXED_CLICKABLE,
			  VALUES_TEXT + FIXED_CLICKABLE,
			  VALUES_HTML + FIXED_CLICKABLE,
			  TRACK + FIXED_CLICKABLE,
			  PEAK_INFO_DIV,
			  MASS_ERROR_TRACK + FIXED_CLICKABLE,
			  HELP + FIXED_CLICKABLE,
              DENOVO_TEXT + FIXED_CLICKABLE,
              DENOVO_HTML + FIXED_CLICKABLE,
			  PEAKLIST_HEADING + FIXED_CLICKABLE,
			  MASS_ERROR_DISPLAY_DIV_ID + FIXED_CLICKABLE);
    }

    resetArrowPositions();

	// Make arrows children of the tracks so that they are correctly positioned
	// and do not disappear when the background is clicked.
	dd.elements.track.addChild(LEFT_MZ_BOUND);
	dd.elements.track.addChild(RIGHT_MZ_BOUND);
	dd.elements.track.addChild(PAN_ZOOM_WINDOW);
	dd.elements.track.addChild(INTENSITY_BOUND);

	dd.elements.massErrorTrack.addChild(MASS_ERROR_POINTER);

	// Set the bound starting points relative to the track.
	dd.elements.leftBound.defx = dd.elements.track.x + POINTER_CORRECTION;
	dd.elements.panZoomWindow.defx = dd.elements.leftBound.x - POINTER_CORRECTION + ARROW_BASE_WIDTH_CORRECTION;
	dd.elements.rightBound.defx = dd.elements.track.x + TRACK_WIDTH + POINTER_CORRECTION;
	dd.elements.intensityBound.defy = dd.elements.track.y + POINTER_CORRECTION;
	dd.elements.massPointer.defx = dd.elements.massErrorTrack.x + MASS_ERROR_TRACK_CORRECTION + POINTER_CORRECTION;

	// Move the massPointer to the correct start position.
	dd.elements.massPointer.moveTo(dd.elements.massErrorTrack.x + MASS_ERROR_TRACK_CORRECTION + POINTER_CORRECTION + massErrorXPos, dd.elements.massErrorTrack.y + 30);
}

/*
  This function is called while a widget is being dragged.  Only respondes to zooming widgets
  to allow the current level of zoom to be displayed.
*/
function my_DragFunc(){
	if (dd.obj.name == LEFT_MZ_BOUND){
		moveLeft(false);
	}
	else if (dd.obj.name == RIGHT_MZ_BOUND){
		moveRight(false);
	}
	else if (dd.obj.name == INTENSITY_BOUND){
		moveIntensityZoom(false);
	}
	else if (dd.obj.name == MASS_ERROR_POINTER){
		massErrorPointerMoved(false);
	}
	else if (dd.obj.name == PAN_ZOOM_WINDOW){
	    panZoomMoved(false);
    }
}

/*
  This function is called when a widget is depressed (mouse down event).  If the widget is a button,
  make it look depressed...
*/
function my_PickFunc(){
	if (dd.obj.name == ZOOM_OUT ||
		dd.obj.name == PRINTER ||
		dd.obj.name == GRID ||
		dd.obj.name == ANNOTATION ||
		dd.obj.name == VALUES_TEXT ||
		dd.obj.name == VALUES_HTML ||
		dd.obj.name == HELP ||
		dd.obj.name == DENOVO_TEXT ||
		dd.obj.name == DENOVO_HTML ||
		dd.obj.name == RESET_SELECTED_PEAKS){

		buttonDown(dd.obj);
	}
}

// Detect the event of a widget being dropped, determine which widget it is and
// respond accordingly...(Also make buttons look released).
function my_DropFunc(){
	if (dd.obj.name == LEFT_MZ_BOUND){
		moveLeft(true);
	}
	else if (dd.obj.name == RIGHT_MZ_BOUND){
		moveRight(true);
	}
	else if (dd.obj.name == INTENSITY_BOUND){
		moveIntensityZoom(true);
	}
	else if (dd.obj.name == PAN_ZOOM_WINDOW){
	    panZoomMoved(true);
    }
	else if (dd.obj.name == ZOOM_OUT){
		buttonUp(dd.obj);
		zoomReset();
	}
	else if (dd.obj.name == PRINTER){
		buttonUp(dd.obj);
		printSpec();
	}
	else if (dd.obj.name == GRID){
		buttonUp(dd.obj);
		toggleGrid()
	}
	else if (dd.obj.name == ANNOTATION){
		buttonUp(dd.obj);
		toggleAnnotation();
	}
	else if (dd.obj.name == VALUES_TEXT){
		buttonUp(dd.obj);
		viewValues(true);
	}
	else if (dd.obj.name == VALUES_HTML){
		buttonUp(dd.obj);
		viewValues(false);
	}
	else if (dd.obj.name == MASS_ERROR_POINTER){
		massErrorPointerMoved(true);
	}
	else if (dd.obj.name == RESET_SELECTED_PEAKS){
		buttonUp(dd.obj)
		resetSelectedPeaks();
	}
	else if (dd.obj.name == HELP){
		buttonUp(dd.obj)
		displayHelp();
	}
	else if (dd.obj.name == DENOVO_TEXT){
	    buttonUp(dd.obj)
		outputDeNovoSequence(DISPLAY_PLAIN_TEXT);
    }
    else if (dd.obj.name == DENOVO_HTML){
	    buttonUp(dd.obj)
		outputDeNovoSequence(DISPLAY_HTML);
    }
}

/*
* Changes the appearance of a button on the mouse down event to provide user feedback -
* moves it down, right and changes the background color to make the button look depressed.
*/
function buttonDown(button){
	button.moveBy(2, 2);
}

/*
* Changes the appearance of a button on the mouse up event to provide user feedback -
* moves it up, left and changes the background color to make the button look released.
*/
function buttonUp(button){
	button.moveBy(-2,-2);
}

/*
* Changes the zoom on the intensity axis when the left mz zoom widget is moved.
*/
function moveLeft(completeDraw){
	if (dd.elements.leftBound.x >= dd.elements.rightBound.x){
		dd.elements.leftBound.moveTo(dd.elements.rightBound.x -1, dd.elements.leftBound.y);
	}
	mzZoom(completeDraw);
}

/*
* Changes the zoom on the intensity axis when the right mz zoom widget is moved.
*/
function moveRight(completeDraw){
	if (dd.elements.leftBound.x >= dd.elements.rightBound.x){
		dd.elements.rightBound.moveTo(dd.elements.leftBound.x + 1, dd.elements.rightBound.y);
	}
	mzZoom(completeDraw);
}

function panZoomMoved(completeDraw){
    dd.elements.leftBound.moveTo (dd.elements.panZoomWindow.x + POINTER_CORRECTION - ARROW_BASE_WIDTH_CORRECTION, dd.elements.leftBound.y);
    dd.elements.rightBound.moveTo (dd.elements.panZoomWindow.x + dd.elements.panZoomWindow.w - 13, dd.elements.rightBound.y);
    calculateMzZoomLimits();
	drawSpectrogram(false, completeDraw);
}

// Re-calculates left and right zoom values and re-draws the spectra.
function mzZoom(completeDraw){
	calculateMzZoomLimits();

    // Re-position the pan tool
    dd.elements.panZoomWindow.moveTo(dd.elements.leftBound.x + 8, dd.elements.panZoomWindow.y);
	// Change the width of the pan tool
	dd.elements.panZoomWindow.resizeTo(dd.elements.rightBound.x - dd.elements.leftBound.x + 5, 4);
	// Change the pan range of the pan tool.
	//dd.elements.panZoomWindow.maxoffl = leftMzZoom;
	dd.elements.panZoomWindow.maxoffr = TRACK_WIDTH - rightMzZoom + leftMzZoom;

	if (dd.elements.panZoomWindow.w == 105){
	    dd.elements.panZoomWindow.hide();
    }
    else {
	    dd.elements.panZoomWindow.show();
	}
	drawSpectrogram(false, completeDraw);
}

function calculateMzZoomLimits(){
    leftMzZoom = dd.elements.leftBound.x - dd.elements.leftBound.defx;
	rightMzZoom = TRACK_WIDTH + (dd.elements.rightBound.x - dd.elements.rightBound.defx);
}

/*
  Changes the zoom on the intensity axis according to the position of the intensity zoom widget.
  Note that this is now logarithmic to allow the user to examine the noise peaks more easily.
*/
function moveIntensityZoom(completeDraw){
	intensityZoom = Math.pow (1.0009214583193, 200 * (TRACK_HEIGHT - (dd.elements.intensityBound.y - dd.elements.intensityBound.defy)));
	drawSpectrogram(false, completeDraw);
}

// Reset the zoom to completely zoomed out.
function zoomReset(){
	leftMzZoom = 0;
	rightMzZoom = TRACK_WIDTH;
	intensityZoom = MAX_INTENSITY_FACTOR;
	resetArrowPositions();
	drawSpectrogram(false, true);
}

// Redraw the spectrum at current zoom for printing,
// open print dialogue.
function printSpec(){
	drawSpectrogram(true, true);
	window.print();
}

// Turns the grid on / off and changes the icon accordingly.
function toggleGrid(){
	if (grid){
		grid = false;
		dd.elements.grid.swapImage("/msdapl/images/pride/spectrumViewer/gridOff.gif");
	}
	else {
		grid = true;
		dd.elements.grid.swapImage("/msdapl/images/pride/spectrumViewer/gridOn.gif");
	}
	drawSpectrogram(false, true);
}

/*
* Turns on / off the annotation of the top n peaks with mz / intensity values.
*/
function toggleAnnotation(){
	if (annotate){
		annotate = false;
		document.getElementById("mzKey").style.display="none";
		document.getElementById("intenKey").style.display="none";
		dd.elements.annotation.swapImage("/msdapl/images/pride/spectrumViewer/annotationOff.gif");
	}
	else {
		annotate = true;
		document.getElementById("mzKey").style.display="block";
		document.getElementById("intenKey").style.display="block";
		dd.elements.annotation.swapImage("/msdapl/images/pride/spectrumViewer/annotationOn.gif");
	}
	drawSpectrogram(false, true);
}

// Called when window resized.
function drawSpectrogramUnprintable(){
	drawSpectrogram (false, true);
}

/*
  Displays m/z and intensity values in tab-delim text file (new window).
*/
function viewValues(asText){
	valuesWindow = window.open("", "mzIntenValues","height=600,width=600,left=100,top=100,resizable=yes,scrollbars=yes,menubar=yes,status=yes");
	if (valuesWindow.focus){
		valuesWindow.focus();
	}

	if (asText){
		valuesWindow.document.open("text/plain");
		valuesWindow.document.write(graphTitle);
		valuesWindow.document.writeln(" - tab separated values file.");
		valuesWindow.document.writeln();
		valuesWindow.document.writeln("m/z\tIntensity");
		for (indexer = 0; indexer < lineCount; indexer++){
			valuesWindow.document.write (mz[indexer]);
			valuesWindow.document.write ("\t");
			valuesWindow.document.writeln (inten[indexer]);
		}
		valuesWindow.document.close();
		if (dd.ie){
			valuesWindow.alert ("Use the menu item 'File'..'Save Target As' to save this as a text file.");
		}
		else if (dd.op){
			valuesWindow.alert ("Use the menu item 'File'..'Save As' to save this as a text file.");
		}
		else{
			valuesWindow.alert ("Use the menu item 'File'..'Save Page As' to save this as a text file.");
		}
	}
	else {
		valuesWindow.document.open("text/html");
		valuesWindow.document.writeln("<html><head><style>th,td{font-family:ariel,sans-serif;font-size:11;}</style><title>");
		valuesWindow.document.writeln(graphTitle);
		valuesWindow.document.writeln("</title></head><body><h3>");
		valuesWindow.document.writeln(graphTitle);
		valuesWindow.document.writeln("</h3><br/><table border='1'>");
		for (indexer = 0; indexer < lineCount; indexer++){
			if (indexer % 20 == 0){
				valuesWindow.document.writeln("<tr><th align = 'left' style='padding-right:50;'>m/z</th><th align = 'left'>Intensity</th></tr>");
			}
			valuesWindow.document.write ("<tr><td align = 'left' style='padding-right:50;'>");
			// Adjust colour to indicate intensity.
			var relativeIntensity = inten[indexer] / maxInten;
			var intensityStyle = (relativeIntensity > 0.9) ? "color:#000000;font-size:12;" :
								  (relativeIntensity > 0.8) ? "color:#111111;font-size:12;" :
								  (relativeIntensity > 0.7) ? "color:#222222;font-size:11;" :
								  (relativeIntensity > 0.6) ? "color:#333333;font-size:11;" :
								  (relativeIntensity > 0.5) ? "color:#444444;font-size:11;" :
								  (relativeIntensity > 0.4) ? "color:#555555;font-size:11;" :
								  (relativeIntensity > 0.3) ? "color:#666666;font-size:10;" :
								  (relativeIntensity > 0.2) ? "color:#777777;font-size:10;" :
								  (relativeIntensity > 0.1) ? "color:#888888;font-size:10;" :
															  "color:#999999;font-size:10;";
			valuesWindow.document.write ("<span style=\"" + intensityStyle + "\">");

			valuesWindow.document.writeln (mz[indexer]);
			valuesWindow.document.write ("</span></td><td align = 'left'>");
			valuesWindow.document.write ("<span style='" + intensityStyle + "'>");
			valuesWindow.document.writeln (inten[indexer]);
			valuesWindow.document.writeln ("</span></td></tr>");
		}
		valuesWindow.document.writeln("</table></html>");
		valuesWindow.document.close();
	}
}


/*
  Opens new window / tab containing help for the spectrum viewer.
*/
function displayHelp(){
	helpWindow = window.open("/pride/viewSpectrumHelp.do", "helpPage","height=600,width=800,resizable=yes,scrollbars=yes,menubar=no,status=yes");
	if (helpWindow.focus){
		helpWindow.focus();
	}
}

/*
  Sets up the mouse listener to listen for mouse move events (to get the
  X / Y coordinates of the mouse) and to listen for mouse click events.
*/
function captureMouseMove (isOn){
	if (isOn){
		if (dd.ie){
            document.attachEvent ("onmousemove", getMouseXY);
            document.attachEvent ("onmousedown", peakSelect);
        }
        else {
			document.captureEvents(Event.MOUSEMOVE)
            document.addEventListener ("mousemove", getMouseXY, true);
            document.addEventListener ("mousedown", peakSelect, true);
		}
	}
	else {
	    if (dd.ie){
            document.detachEvent ("onmousemove", getMouseXY);
            document.detachEvent ("onmousedown", peakSelect);
        }
        else {
			document.captureEvents(Event.MOUSEMOVE)
            document.removeEventListener ("mousemove", getMouseXY, true);
            document.removeEventListener ("mousedown", peakSelect, true);
		}
		document.getElementById ("spectrogramCanvas").removeEventListener ("mousemove", getMouseXY, true);
	}
}

/*
  Listener for mouse movement on the spectrum div.  stores the mouse
  x / y coordinates and displays a pop-up window with details of the
  peak if one is being hovered over.
*/
function getMouseXY(mouseEvent) {
	if (dd.ie) {
	    // Note - +2 correction below to allow correct peak selection in IE.
		mouseX = event.clientX + document.body.scrollLeft;
		mouseY = event.clientY + document.body.scrollTop;
	}
	else {
		mouseX = mouseEvent.pageX;
		mouseY = mouseEvent.pageY;
	}
	if (mouseX < 0){mouseX = 0;}
	if (mouseY < 0){mouseY = 0;}
	displayedPeak = mzToPeakHash["" + mouseX];

    tooltipX = mouseX + 5;
    tooltipY = mouseY - 5;
	if (tooltipX + 145 > winW){
		tooltipX = mouseX - 145;
	}
	if (tooltipY + dd.elements.peakInfoDiv.h + 20 > winH){
		tooltipY = mouseY - (dd.elements.peakInfoDiv.h + 20);
	}
	dd.elements.peakInfoDiv.moveTo (tooltipX, tooltipY);
	if (mouseY < MINIMUM_HEADING_WIDTH || mouseY > winH * 0.9 || mouseX > winW - MINIMUM_RIGHT_SIDE_WIDTH) {
	    dd.elements.peakInfoDiv.hide();
	}

	else if (displayedPeak != null && displayedPeak.y1 - (displayedPeak.y2 - displayedPeak.y1) < mouseY){
		displayPeakDetails (displayedPeak);
		if (dd.elements.peakInfoDiv.y + dd.elements.peakInfoDiv.h + 20 > winH){
			tooltipY = mouseY - (dd.elements.peakInfoDiv.h + 20);
			dd.elements.peakInfoDiv.moveTo (dd.elements.peakInfoDiv.x, dd.elements.peakInfoDiv.y - (dd.elements.peakInfoDiv.h + 30))
		}
	}
}

/*
  If an attempt is made to click a peak, this method detects if a peak has
  been clicked (with an error of one pixel either side of the peak.)

  The status of the peak is then changed.  The transition depends on the
  current status of the peak:
  1. Start state NOT Selected and NOT in selectedPeaksArray -> Selected and placed into selectedPeaksArray
  2. In selectedPeaksArray but NOT selected -> Still in selectedPeaksArray AND Selected.
  3. Selected and in selectedPeaksArray -> NOT selected and REMOVED from selectedPeaksArray.

*/
function peakSelect(mouseEvent){

    // Do not include any clicks above the spectrum area (for the current series).
    if ( ! deNovoAllowed || mouseY < MINIMUM_HEADING_WIDTH - SELECTED_PEAK_ARROW_ELEVATION[currentSeries] ) return;

	clickedPeak = mzToPeakHash["" + mouseX];
    xError = 0;
	// If not clicked on an actual peak, check if one to the left or right provides a valid peak.
	if (clickedPeak == null || (clickedPeak.y1 > mouseY && selectedPeaksArray[currentSeries]["" + clickedPeak.mz] == null)){
	    leftPeak = mzToPeakHash["" + (mouseX - 1)];
	    replaced = false;
        if (leftPeak != null && (leftPeak.y1 < mouseY || selectedPeaksArray[currentSeries]["" + leftPeak.mz] != null)){
        	clickedPeak = leftPeak;
        	replaced = true;
		}
	    rightPeak = mzToPeakHash["" + (mouseX + 1)];
		if (rightPeak != null && (rightPeak.y1 < mouseY || selectedPeaksArray[currentSeries]["" + rightPeak.mz] != null)){
			// If there is a valid peak on both sides, do not select either as ambiguous.
        	if (replaced) return;
        	replaced = true;
        	clickedPeak = rightPeak;
		}
		if (!replaced) return;
	}
	hasSelectedPeak = true;
	if (clickedPeak == null) return;
	if (selectedPeaksArray[currentSeries]["" + clickedPeak.mz] != null){
		// Already selected - if it is the 'currently selected'
		// then de-select, othewise set to currently selected.
		if (lastSelectedPeak[currentSeries] != null && clickedPeak.mz == lastSelectedPeak[currentSeries].mz){
			selectedPeaksArray[currentSeries]["" + clickedPeak.mz]=null;
			lastSelectedPeak[currentSeries] = null;
		}
		else {
			lastSelectedPeak[currentSeries] = clickedPeak;
		}
		drawSpectrogram(false, true);  // Not printable, complete.
	}
	else{
		// Not selected, and peak has been clicked, so add to selectedPeaksArray
		displayPeakDetails (clickedPeak);
		selectedPeaksArray[currentSeries]["" + clickedPeak.mz] = clickedPeak;
		lastSelectedPeak[currentSeries] = clickedPeak;
		drawSpectrogram(false, true);  // Not printable, complete.
	}
}

var DISPLAY_PLAIN_TEXT = 0;
var DISPLAY_HTML = 1;
var DISPLAY_EXCEL = 2;

/*
  Outputs the results of a de novo sequence as either plain text or HTML.
*/
function outputDeNovoSequence(type){
	valuesWindow = window.open("", "deNovoSequence","height=600,width=600,left=100,top=100,resizable=yes,scrollbars=yes,menubar=yes,status=yes");
	if (valuesWindow.focus){
		valuesWindow.focus();
	}
	var out;
	var header = null;
	var sequenceHeading = null;
	var sequenceSeparator = null;
	var sequenceFooter =  null;
	var detailHeading = new Array(2);
	detailHeading[0] = null;
	detailHeading[1] = null;
	var tableFooter = null;
	var footer = null;
	if (type==DISPLAY_PLAIN_TEXT){
	    out = valuesWindow.document.open("text/plain");
	    header = "De novo sequence prediction based upon '" + graphTitle + "'\n"
	    sequenceHeading = "Predicted Sequence (Y ions)\tPredicted Sequence (B ions)\n";
	    sequenceSeparator = "\t";
	    sequenceFooter = "\nMass Error: " + Molecule.MASS_ERROR_DALTONS + " Daltons.\n";
	    detailHeading[0] = "\nY Series Ions\nStart m/z\tEnd m/z\tm/z Delta\tPrediction(s)\n";
	    detailHeading[1] = "\nB Series Ions\nStart m/z\tEnd m/z\tm/z Delta\tPrediction(s)\n";
	    tableFooter = "\n";
	    footer = "";
    }
    else if (type != DISPLAY_PLAIN_TEXT){

        mimeType = (type==DISPLAY_HTML) ? "text/html" : "application/vnd.ms-excel";
        out = valuesWindow.document.open(mimeType);
        header = "<html><head><style>th,td{font-family:ariel,sans-serif;font-size:11;}</style><title>" +
	             "De novo sequence prediction</title></head><body><h3>" +
	             "<i>De novo</i> sequence prediction based upon '" + graphTitle + "'" +
	             "</h3><br/><table border='1'>";
	    sequenceHeading = "<tr><th>Predicted Sequence: Y ions</th><th>Predicted Sequence: B ions</th><th>Mass Error (Daltons)</th></tr><tr><td>";
	    sequenceSeparator = "</td><td>";
	    sequenceFooter = "</td><td>" + Molecule.MASS_ERROR_DALTONS + "</td></tr></table><br/>";
	    detailHeading[0] = "<table border = '1'><tr><th colspan ='4'>Y Series Ions</th></tr><tr><th>Start m/z</th><th>End m/z</th><th>m/z Delta</th><th>Prediction(s)</th></tr>";
	    detailHeading[1] = "<table border = '1'><tr><th colspan ='4'>B Series Ions</th></tr><tr><th>Start m/z</th><th>End m/z</th><th>m/z Delta</th><th>Prediction(s)</th></tr>";
        tableFooter = "</table><br/>";
        footer = "</body></html>";
    }
    // Add on other types...

	lineCount = Math.min (mz.length, inten.length);
	var sequenceString = new Array(2);
	var detailString = new Array(2);
	for (seriesIndex = 0; seriesIndex <= 1; seriesIndex++){
	var lastPeak = null;
	    sequenceString[seriesIndex] = "";
	    detailString[seriesIndex] = "";
	for (indexer = 0; indexer < lineCount; indexer++){
            selectedPeak = selectedPeaksArray[seriesIndex]["" + (mz[indexer])];
		if (selectedPeak != null){
		    // Ok - found a selected peak.
		    if (lastPeak != null){
		        if (type!=DISPLAY_PLAIN_TEXT){
                        detailString[seriesIndex] += "<tr><td>";
                }
		        // Got two peaks, so write out a line.
                    detailString[seriesIndex] += lastPeak.mz;
                    detailString[seriesIndex] += (type==DISPLAY_PLAIN_TEXT) ? "\t" : "</td><td>";
                    detailString[seriesIndex] += selectedPeak.mz;
                    detailString[seriesIndex] += (type==DISPLAY_PLAIN_TEXT) ? "\t" : "</td><td>";
                    detailString[seriesIndex] += (selectedPeak.mz - lastPeak.mz);
                // determine if the mass difference signifies anything...
                matches = returnMatchArray (selectedPeak.mz - lastPeak.mz);
                // for each match, write out details on the same line.
                if (type!=DISPLAY_PLAIN_TEXT){
                        detailString[seriesIndex] += "</td><td>";
                }
                multipleMatches = matches.length > 1;
                    var nextSequenceElement = "";
                if (matches.length > 0){
                    if (multipleMatches){
                            nextSequenceElement += "[";
                    }
                    for (var matchIndex = 0; matchIndex < matches.length; matchIndex++){
                        molecule = matches [matchIndex];
                        if (matchIndex > 0){
                                nextSequenceElement += ",";
                            if (type != DISPLAY_PLAIN_TEXT){
                                    detailString[seriesIndex] +="; ";
                            }
                        }
                            nextSequenceElement += molecule.oneLetterCode;
                        molecule = matches[matchIndex];
                            detailString[seriesIndex] += (type==DISPLAY_PLAIN_TEXT) ? "\t" : "";
                            detailString[seriesIndex] += molecule.oneLetterCode;
                            detailString[seriesIndex] += " (";
                            detailString[seriesIndex] += molecule.name;
                            detailString[seriesIndex] += (")");
                        if (molecule.z != null && molecule.z > 1){
                                detailString[seriesIndex] += " z=+" + molecule.z;
                        }
                    }
                    if (multipleMatches){
                            nextSequenceElement += "]";
                    }
                        // Reverse the order of Y ions.
                        if (seriesIndex == 0){
                            sequenceString[0] = nextSequenceElement + sequenceString[0];
                }
                        else {
                            sequenceString[1] = sequenceString[1] + nextSequenceElement;
            }
                    }
                    detailString[seriesIndex] += (type==DISPLAY_PLAIN_TEXT) ? "\n" : "</td></tr>";
                }
            lastPeak = selectedPeak;
		}
	}
	}
	valuesWindow.document.write (header);
	valuesWindow.document.write (sequenceHeading);
	valuesWindow.document.write (sequenceString[0]);
	valuesWindow.document.write (sequenceSeparator);
	valuesWindow.document.write (sequenceString[1]);
	valuesWindow.document.write (sequenceFooter);
	valuesWindow.document.write (detailHeading[0]);
	valuesWindow.document.write (detailString[0]);
	valuesWindow.document.write (tableFooter);
	valuesWindow.document.write (detailHeading[1]);
	valuesWindow.document.write (detailString[1]);
	valuesWindow.document.write (tableFooter);
	valuesWindow.document.write (footer);
	valuesWindow.document.close();
}

/*
  Updates the floating div (that follows the cursor around the spectrum)
  with the mz / intensity values of the current spectrum, plus
  details of the mass delta / amino acid matches if appropriate.
*/
function displayPeakDetails (displayedPeak){
    var outputString = "";

    // If there is any external annotation (label) add it here.
    if (labelArray != null && (label = labelArray[displayedPeak.arrayIndex]) != "-"){
        if (colourArray != null && colourArray[displayedPeak.arrayIndex] != null && colourArray[displayedPeak.arrayIndex] != ""){
            outputString += "<span style='color:" + colourArray[displayedPeak.arrayIndex] + ";'><b>" + label + "</b></span><br/>";
        }
        else {
            outputString += label + "<br/>";
        }
        
    }

    outputString += "<b>m/z: " + displayedPeak.mz.toFixed(3) + "<br/>Intensity: " + displayedPeak.intensity + "</b><br/>";
	document.getElementById("mzDisplay").value=displayedPeak.mz;
	document.getElementById("intenDisplay").value=displayedPeak.intensity;

    if (additionalArray != null && (additionalArray[displayedPeak.arrayIndex]) != ""){
        outputString += additionalArray[displayedPeak.arrayIndex] + "<br/>";
    }


    // Look for identifiable mass differences.
	if (hasSelectedPeak){
		if (lastSelectedPeak[currentSeries] != null){
			mzDelta = Math.abs(lastSelectedPeak[currentSeries].mz - displayedPeak.mz);
            diffString = createMatchHTML (mzDelta);
            outputString += "Delta: " + diffString;
        }
		else {
			outputString += "<b><i>No Current Peak<br/>to measure delta from.</i></b>";
		}
		outputString += currentSeriesMessageHtml[currentSeries];
	}
	else if (deNovoAllowed) {
		outputString += "<br/><b>Click on a peak<br/>to begin <i>de novo</i><br/>sequencing</b>";
	}
	dd.elements.peakInfoDiv.write (outputString);
	if (dd.elements.peakInfoDiv.text == null || dd.elements.peakInfoDiv.text == "" || mouseY < MINIMUM_HEADING_WIDTH || mouseY > winH * 0.9 || mouseX > winW - MINIMUM_RIGHT_SIDE_WIDTH) {
	    dd.elements.peakInfoDiv.hide();
	}
	else {
	    dd.elements.peakInfoDiv.show();
	}
}

/*
  Allows the user to view and modify the mass error.  Captures spurious values.
*/
function updateError (inputBox){
	if (inputBox.value == null || inputBox.value == "" ||  isNaN(inputBox.value) || inputBox.value < 0 || inputBox.value >= 10){
	    window.alert ("Sorry - that is an invalid value. (Must be a number >= 0 and < 10)");
		inputBox.value = Molecule.MASS_ERROR_DALTONS;
	}
	else {
		Molecule.MASS_ERROR_DALTONS = inputBox.value;
		newPointerPosition = massErrorToScale (Molecule.MASS_ERROR_DALTONS);
		dd.elements.massPointer.moveTo  (Math.max(dd.elements.massPointer.defx,newPointerPosition + dd.elements.massPointer.defx), dd.elements.massPointer.y);
		drawSpectrogram(false, true);  // Not printable, complete.
	}
}

/*
   Handles movement of the widget controlling the mass error.
   Modifies and displays the MASS_ERROR_DALTONS class variable
*/
function massErrorPointerMoved(reDraw){
    pointerPosition = dd.elements.massPointer.x - dd.elements.massPointer.defx;
    newErrorValue = scaleToMassError (pointerPosition);
    Molecule.MASS_ERROR_DALTONS = newErrorValue;
    document.getElementById (MASS_ERROR_DISPLAY_ID).value = newErrorValue;
    if (reDraw){
        drawSpectrogram(false, true);  // Not printable, complete.
	}
}

/*
  Removes all reference to selected peaks and re-draws the spectrum.
*/
function resetSelectedPeaks(){
    selectedPeaksArray[0] = new Array();
    selectedPeaksArray[1] = new Array();
    lastSelectedPeak[0] = null;
    lastSelectedPeak[1] = null;
    drawSpectrogram(false, true);  // Not printable, complete.
}

/*
  If the user changes the de novo sequencing parameter 'maximum charge state', update
  the value and redraw the spectrum accordingly.
*/
function setCharge(checkBox){
    oldMax = Molecule.MAX_CHARGE_INCLUDED;
	Molecule.MAX_CHARGE_INCLUDED = (checkBox.checked) ? 3 : 1;
	if (oldMax != Molecule.MAX_CHARGE_INCLUDED){
	drawSpectrogram(false, true);  // Not printable, complete.
}
}

/*
  Currently selected ion series (that the user is working on) changed.
  Update.  (No need to re-draw the spectrum - it will look no different.)
*/
function seriesChanged(){
    currentSeries = (document.ionSeriesForm.ionSeries[0].checked) ? 0 : 1;  // 0 = y series, 1 = b series
}

/*
  Function to initialize the spectrum viewer.
*/
function init(){
    // Set the value of the mass error field
    document.getElementById(MASS_ERROR_DISPLAY_ID).value=Molecule.MASS_ERROR_DALTONS;

    // Set the current series as selected.
    seriesChanged()

	// Draw the spectrum for the first time...
	drawSpectrogram(false, true);

	// Create the widgets for zooming.
	initialiseWidgets();

	// Set the call to re-draw the spectrum if the window is resized.
	window.onresize=drawSpectrogramUnprintable;

	// Capture mouse events to allow mouse over and peak clicking to be captured.
	captureMouseMove (true);
}

/****************************************************\
		And finally get drawing
\****************************************************/

init();

//-->
</script>
</body>
</html>
