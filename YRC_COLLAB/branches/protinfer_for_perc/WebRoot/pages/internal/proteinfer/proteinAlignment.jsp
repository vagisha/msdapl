<%@ taglib uri="/WEB-INF/yrc-www.tld" prefix="yrcwww" %>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html" %>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean" %>
<%@ taglib uri="/WEB-INF/struts-logic.tld" prefix="logic" %>


<%@ include file="/includes/header.jsp" %>

<%@ include file="/includes/errors.jsp" %>

<script type="text/javascript" src="/yrc/js/wz_jsgraphics.js"></script>
<script src="/yrc/js/jquery.ui-1.6rc2/jquery-1.2.6.js"></script>
<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.core.js"></script>
<script type="text/javascript" src="/yrc/js/jquery.ui-1.6rc2/ui/ui.slider.js"></script>

<script type="text/javascript">



$(document).ready(function() {
	var jg = new jsGraphics("alignment");
    myDrawFunction(jg);
    
    $("#content-slider").slider({
    	animate: true,
    	handle: ".content-slider-handle",
    	change: handleSliderChange,
    	slide: handleSliderSlide
  	});
});

function handleSliderChange(e, ui)
{
  var maxScroll = $("#content-scroll").attr("scrollWidth") -
                  $("#content-scroll").width();
  $("#content-scroll").animate({scrollLeft: ui.value *
     (maxScroll / 100) }, 1000);
}

function handleSliderSlide(e, ui)
{
  var maxScroll = $("#content-scroll").attr("scrollWidth") -
                  $("#content-scroll").width();
  $("#content-scroll").attr({scrollLeft: ui.value * (maxScroll / 100) });
}


function myDrawFunction(jg)
{
  jg.setColor("#000000"); 
  jg.drawStringRect("<b>Protein1</b>", 0,10,60);
  jg.drawRect(70,10,200,20);
  jg.setColor("#0000ff"); 
  jg.fillRect(270,10,100,21);
  jg.setColor("#000000"); 
  jg.drawRect(370,10,350,20);
  
  jg.drawStringRect("<b>Protein2</b>", 0,40,60);
  jg.drawRect(100,40,200,20);
  jg.setColor("#0000ff"); 
  jg.fillRect(270,40,100,21);
  jg.setColor("#000000"); 
  jg.drawRect(370,40,500,20);
  
  
  jg.paint();
}

</script>

<center>
<div id="main" style="width:800px; margin:0 auto;" >

<div id="content-scroll" style="width: 800px; height: 300px; margin-top: 10px; overflow: hidden; border: solid 1px black;">
<div id="content-holder" style="position:relative;height:300px;width:1800px; background-color: #EFEFEF;">

<div id="alignment" style="position:relative;height:300px;width:1800px; background-color: #EFEFEF; float: left;">


</div>

</div>
</div>


<div id="content-slider" style="width: 800px;height: 6px; margin-top: 10px; margin-bottom:20px; background: #AAAAAA; position: relative;">
	<div class="content-slider-handle" style="width: 8px; height: 14px; position: absolute; top: -4px; background: #478AFF;border: solid 1px black;"></div>
</div>

</div>

</center>


<%@ include file="/includes/footer.jsp" %>