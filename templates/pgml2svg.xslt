<?xml version="1.0"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" xmlns:svg="http://www.w3.org/2000/svg" version="1.0">

	<xsl:output method="xml" />
	
	<xsl:template mode="pgml" match="pgml">
		<svg:svg>
			<svg:style type="text/css" href="svg.css"/>
			<svg:color-profile name="color-profile">sRGB</svg:color-profile>
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates/>	
		</svg:svg>
	</xsl:template>
	
	<xsl:template match="group">
		<svg:g>
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates/>	
		</svg:g>
	</xsl:template>


	<xsl:template match="rect|rectangle">
		<svg:rect>
			<xsl:apply-templates select="@x|@y|@height|@width|@rx|@ry|@style"/>
		</svg:rect>
	</xsl:template>

	<xsl:template match="text">
		<svg:text >
			<xsl:apply-templates select="@textsize|@font|@x|@y|@style|@stroke|@strokecolor|@fill|text()"/>
		</svg:text>
	</xsl:template>

	<xsl:template match="line">
		<svg:line>
			<xsl:apply-templates select="@x1|@y1|@x2|@y2|@style"/>
		</svg:line>
	</xsl:template>

	<xsl:template match="polyline">
		<svg:polyline>
			<xsl:apply-templates select="@href|@points|@style"/>
			<xsl:apply-templates/>
		</svg:polyline>
	</xsl:template>

	<xsl:template match="polygon">
		<svg:polygon>
			<xsl:apply-templates select="@href|@points|@style"/>
			<xsl:apply-templates/>
		</svg:polygon>
	</xsl:template>
	
	
	<xsl:template match="path">
		<svg:path>
			<!--<xsl:apply-templates select="@*"/>-->
			<xsl:apply-templates select="@href|@stroke|@fill|@fillcolor|@strokecolor"/>
			<xsl:attribute name="d">
				<xsl:for-each select="node()"><xsl:choose>
						<xsl:when test="name()='moveto'">M<xsl:value-of select="@x"/><xsl:text> </xsl:text> <xsl:value-of select="@y" /></xsl:when>
						<xsl:when test="name()='lineto'"> L<xsl:value-of select="@x"/><xsl:text> </xsl:text> <xsl:value-of select="@y" /></xsl:when>
					</xsl:choose></xsl:for-each>
				</xsl:attribute>
			
		</svg:path>
	</xsl:template>


	<xsl:template match="ellipse">
		<svg:ellipse>
			<xsl:apply-templates select="@href|@rx|@ry|@style|@stroke|@strokecolor|@fill|@fillcolor"/>
			<xsl:attribute name="cx">
				<xsl:value-of select="@x"/>
			</xsl:attribute>
			<xsl:attribute name="cy">
				<xsl:value-of select="@y"/>
			</xsl:attribute>
			
			<xsl:attribute name="color-profile">sRGB</xsl:attribute>
		</svg:ellipse>
	</xsl:template>
	
	<xsl:template match="moveto">
		<svg:moveto>
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates/>
		</svg:moveto>	
	</xsl:template>
	
		<xsl:template match="lineto">
		<svg:lineto>
			<xsl:apply-templates select="@*"/>
			<xsl:apply-templates/>
		</svg:lineto>	
	</xsl:template>
	
  <xsl:template match="text()">
  	<xsl:copy /> 
  </xsl:template>
  
  <xsl:template match="@description|@version|private">
  
  </xsl:template>
  
  <xsl:template match="@*">
  	<xsl:copy /> 
  </xsl:template>
  
  <xsl:template match="@font">
  	<xsl:attribute name="font"><xsl:value-of select="'Arial'" /></xsl:attribute>
  </xsl:template>
  
  
  <xsl:template match="@textsize">
  	<xsl:attribute name="font-size"><xsl:value-of select="number(current())+3" /></xsl:attribute>
  </xsl:template>
  
  <xsl:template match="@fill">
  	<xsl:attribute name="fill"><xsl:value-of select="current()"/></xsl:attribute>
  </xsl:template>
  
   <xsl:template match="@fillcolor">
  	<xsl:attribute name="fill"><xsl:value-of select="current()"/></xsl:attribute>
  </xsl:template>
  
  <xsl:template match="@strokecolor">
  	<xsl:attribute name="stroke"><xsl:value-of select="current()"/></xsl:attribute>
  </xsl:template>
  
  <xsl:template match="@stroke">
  	<xsl:attribute name="stroke-width">1</xsl:attribute>
  </xsl:template>
  
  <xsl:template match="@href">
  	<xsl:attribute name="xlink:href"><xsl:value-of select="current()"/></xsl:attribute>
	<xsl:attribute name="target">detail</xsl:attribute>
  </xsl:template>

</xsl:stylesheet>
