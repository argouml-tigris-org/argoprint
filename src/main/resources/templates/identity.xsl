<?xml version="1.0"?>

<xsl:stylesheet
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
 xmlns:svg="http://www.w3.org/2000/svg">

 <xsl:output method="xml" indent="yes"/>

 <xsl:template match="*">
   <xsl:copy-of select="."/>
 </xsl:template>


</xsl:stylesheet>
