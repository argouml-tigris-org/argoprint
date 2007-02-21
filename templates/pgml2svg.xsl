<?xml version="1.0"?>

<!-- $Id$
Copyright (c) 2006 The Regents of the University of California. All
Rights Reserved. Permission to use, copy, modify, and distribute this
software and its documentation without fee, and without a written
agreement is hereby granted, provided that the above copyright notice
and this paragraph appear in all copies. This software program and
documentation are copyrighted by The Regents of the University of
California. The software program and documentation are supplied "AS
IS", without any accompanying services from The Regents. The Regents
does not warrant that the operation of the program will be
uninterrupted or error-free. The end-user understands that the program
was developed for research purposes and is advised not to rely
exclusively on the program for any reason. IN NO EVENT SHALL THE
UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR DIRECT, INDIRECT,
SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING LOST PROFITS,
ARISING OUT OF THE USE OF THIS SOFTWARE AND ITS DOCUMENTATION, EVEN IF
THE UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF
SUCH DAMAGE. THE UNIVERSITY OF CALIFORNIA SPECIFICALLY DISCLAIMS ANY
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE. THE SOFTWARE
PROVIDED HEREUNDER IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
CALIFORNIA HAS NO OBLIGATIONS TO PROVIDE MAINTENANCE, SUPPORT,
UPDATES, ENHANCEMENTS, OR MODIFICATIONS. -->

<!-- Not a general PGML2SVG transformation, for ArgoUML PGML output only. -->

<xsl:stylesheet
 xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0"
 xmlns:svg="http://www.w3.org/2000/svg">

 <xsl:output method="xml" indent="yes"/>

 <xsl:template match="/">
   <xsl:apply-templates />
 </xsl:template>

 <xsl:template match="pgml">
  <svg:svg version="1.1">
    <xsl:call-template name="attr.rules" />
    <xsl:apply-templates />
  </svg:svg>
 </xsl:template>

 <xsl:template match="group">
  <svg:g>
   <xsl:call-template name="attr.rules" />
   <xsl:apply-templates />
  </svg:g>
 </xsl:template>

 <xsl:template match="rectangle">
  <svg:rect>
   <xsl:call-template name="attr.rules" />
   <xsl:apply-templates />
  </svg:rect>
 </xsl:template>

 <xsl:template match="circle">
  <svg:rect>
   <xsl:call-template name="attr.rules" />
   <xsl:apply-templates />
  </svg:rect>
 </xsl:template>

 <xsl:template match="ellipse">
  <svg:ellipse>
   <xsl:call-template name="attr.rules" />
   <xsl:apply-templates />
  </svg:ellipse>
 </xsl:template>

 <xsl:template match="text">
  <svg:text>
   <xsl:call-template name="attr.rules" />

   <xsl:attribute name="dominant-baseline">
    <xsl:text>hanging</xsl:text>
   </xsl:attribute>

   <xsl:value-of select="text()" />
   <xsl:apply-templates />
  </svg:text>
 </xsl:template>

 <xsl:template match="path">
  <svg:path>
   <xsl:call-template name="attr.rules" />
   <xsl:attribute name="d">
    <xsl:apply-templates />
   </xsl:attribute>
  </svg:path>
 </xsl:template>

 <xsl:template match="moveto">
    <xsl:text>M</xsl:text>
    <xsl:text> </xsl:text>
    <xsl:value-of select="@x" />
    <xsl:text> </xsl:text>
    <xsl:value-of select="@y" />
    <xsl:text> </xsl:text>
 </xsl:template>
 <xsl:template match="lineto">
    <xsl:text>L</xsl:text>
    <xsl:text> </xsl:text>
    <xsl:value-of select="@x" />
    <xsl:text> </xsl:text>
    <xsl:value-of select="@y" />
    <xsl:text> </xsl:text>
 </xsl:template>
 <xsl:template match="closepath">
   Z
 </xsl:template>

 <xsl:template name="attr.rules">
   <xsl:if test="@x">
    <xsl:choose>
     <xsl:when test="local-name()='ellipse'">
      <xsl:attribute name="cx">
       <xsl:value-of select="@x" />
      </xsl:attribute>
     </xsl:when>
     <xsl:otherwise>
      <xsl:attribute name="x">
       <xsl:value-of select="@x" />
      </xsl:attribute>
     </xsl:otherwise>
    </xsl:choose>
   </xsl:if>

   <xsl:if test="@y">
    <xsl:choose>
     <xsl:when test="local-name()='ellipse'">
      <xsl:attribute name="cy">
       <xsl:value-of select="@y" />
      </xsl:attribute>
     </xsl:when>
     <xsl:otherwise>   
      <xsl:attribute name="y">
       <xsl:value-of select="@y" />
      </xsl:attribute>
     </xsl:otherwise>
    </xsl:choose>
   </xsl:if>
   <xsl:if test="@width">
    <xsl:attribute name="width">
     <xsl:value-of select="@width" />
    </xsl:attribute>
   </xsl:if>
   <xsl:if test="@height">
    <xsl:attribute name="height">
     <xsl:value-of select="@height" />
    </xsl:attribute>
   </xsl:if>
   <xsl:if test="@font">
    <xsl:attribute name="font-family">
     <xsl:value-of select="@font" />
    </xsl:attribute>
   </xsl:if>
   <xsl:if test="@textsize">
    <xsl:attribute name="font-size">
     <xsl:value-of select="@textsize" />
    </xsl:attribute>
   </xsl:if>

   <xsl:choose>
    <xsl:when test="@fill=1">
     <xsl:if test="@fillcolor">
      <xsl:attribute name="fill">
       <xsl:value-of select="@fillcolor" />
      </xsl:attribute>
     </xsl:if>
    </xsl:when>
    <xsl:otherwise>
     <xsl:attribute name="fill">none</xsl:attribute>
    </xsl:otherwise>
   </xsl:choose>

   <xsl:if test="@stroke=1">
    <xsl:if test="@strokecolor">
     <xsl:attribute name="stroke">
      <xsl:value-of select="@strokecolor" />
     </xsl:attribute>
    </xsl:if>
   </xsl:if>

   <xsl:if test="@visibility=0">
    <xsl:attribute name="opacity">
     <xsl:text>0</xsl:text>
    </xsl:attribute>
   </xsl:if>

   <xsl:if test="@rounding">
    <xsl:attribute name="rx">
     <xsl:value-of select="@rounding" />
    </xsl:attribute>
    <xsl:attribute name="ry">
     <xsl:value-of select="@rounding" />
    </xsl:attribute>
   </xsl:if>

   <xsl:if test="@rx">
    <xsl:attribute name="rx">
     <xsl:value-of select="@rx" />
    </xsl:attribute>
   </xsl:if>

   <xsl:if test="@ry">
    <xsl:attribute name="ry">
     <xsl:value-of select="@ry" />
    </xsl:attribute>
   </xsl:if>
 </xsl:template>

<!--  overwrite built-in template, do not copy anything unspecified -->
 <xsl:template match="text()|@*">
 </xsl:template>

</xsl:stylesheet>
