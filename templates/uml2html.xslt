<xsl:stylesheet version="1.0" 
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
	xmlns="http://www.w3.org/1999/xhtml" 
	xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:UML="org.omg.xmi.namespace.UML"
	xmlns:xlink="http://www.w3.org/1999/xlink"
	id="uml2html"
>
<xsl:import href="pgml2svg.xslt"/>
<xsl:import href="pgml2html.xslt"/>

<xsl:output doctype-public="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd" encoding="UTF-8" indent="yes" method="xml" omit-xml-declaration="no"/>

<xsl:template match="/">
<html>
<head>
	<link href="/medea_server/css/argo.css" rel="stylesheet" type="text/css" />
	
</head>
<body>

<p>
<xsl:value-of select="//documentation/description"/>
</p>

	<xsl:for-each select="//members/member[@type='pgml']">
		<xsl:variable name="diagname"><xsl:value-of select="@diagramname"/></xsl:variable>
		<xsl:variable name="diagtypes"><xsl:value-of select="@diagramclass"/></xsl:variable>
		<h2><xsl:value-of select="$diagname"/></h2>
		
		<!-- Create svg diagram -->
		<xsl:apply-templates mode="pgml" select="/uml//pgml[@name=$diagname]"/>
		
		<!-- Extract text of diagram -->
		<xsl:choose>
		<xsl:when test="$diagtypes='org.argouml.uml.diagram.use_case.ui.UMLUseCaseDiagram'">
		<xsl:apply-templates mode="usecase" select="//group"/>
		</xsl:when>
		
		<xsl:when test="$diagtypes='org.argouml.uml.diagram.static_structure.ui.UMLClassDiagram'">
		<xsl:apply-templates mode="class" select="//group"/>
		</xsl:when>
		
		</xsl:choose>
	</xsl:for-each>
	
</body>
</html>

</xsl:template>



</xsl:stylesheet>
