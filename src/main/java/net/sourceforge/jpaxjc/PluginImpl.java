/*
 * Copyright (c) 2009 The JPA-XJC Project. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   o Redistributions of source code must retain the above copyright
 *     notice, this  list of conditions and the following disclaimer.
 *
 *   o Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in
 *     the documentation and/or other materials provided with the
 *     distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE JPA-XJC PROJECT AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE JPA-XJC PROJECT OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * $Id$
 *
 */
package net.sourceforge.jpaxjc;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JBlock;
import com.sun.codemodel.JCatchBlock;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JConditional;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JExpr;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JOp;
import com.sun.codemodel.JTryBlock;
import com.sun.codemodel.JType;
import com.sun.codemodel.JVar;
import net.sourceforge.jpaxjc.ns.persistence.Persistence;
import net.sourceforge.jpaxjc.ns.persistence.orm.AccessType;
import net.sourceforge.jpaxjc.ns.persistence.orm.AssociationOverride;
import net.sourceforge.jpaxjc.ns.persistence.orm.AttributeOverride;
import net.sourceforge.jpaxjc.ns.persistence.orm.Attributes;
import net.sourceforge.jpaxjc.ns.persistence.orm.Basic;
import net.sourceforge.jpaxjc.ns.persistence.orm.CascadeType;
import net.sourceforge.jpaxjc.ns.persistence.orm.Column;
import net.sourceforge.jpaxjc.ns.persistence.orm.ColumnResult;
import net.sourceforge.jpaxjc.ns.persistence.orm.DiscriminatorColumn;
import net.sourceforge.jpaxjc.ns.persistence.orm.Embeddable;
import net.sourceforge.jpaxjc.ns.persistence.orm.EmbeddableAttributes;
import net.sourceforge.jpaxjc.ns.persistence.orm.Embedded;
import net.sourceforge.jpaxjc.ns.persistence.orm.EmptyType;
import net.sourceforge.jpaxjc.ns.persistence.orm.Entity;
import net.sourceforge.jpaxjc.ns.persistence.orm.EntityListener;
import net.sourceforge.jpaxjc.ns.persistence.orm.EntityListeners;
import net.sourceforge.jpaxjc.ns.persistence.orm.EntityMappings;
import net.sourceforge.jpaxjc.ns.persistence.orm.EntityResult;
import net.sourceforge.jpaxjc.ns.persistence.orm.FieldResult;
import net.sourceforge.jpaxjc.ns.persistence.orm.GeneratedValue;
import net.sourceforge.jpaxjc.ns.persistence.orm.GenerationType;
import net.sourceforge.jpaxjc.ns.persistence.orm.Id;
import net.sourceforge.jpaxjc.ns.persistence.orm.IdClass;
import net.sourceforge.jpaxjc.ns.persistence.orm.Inheritance;
import net.sourceforge.jpaxjc.ns.persistence.orm.JoinColumn;
import net.sourceforge.jpaxjc.ns.persistence.orm.JoinTable;
import net.sourceforge.jpaxjc.ns.persistence.orm.ManyToMany;
import net.sourceforge.jpaxjc.ns.persistence.orm.ManyToOne;
import net.sourceforge.jpaxjc.ns.persistence.orm.MappedSuperclass;
import net.sourceforge.jpaxjc.ns.persistence.orm.NamedNativeQuery;
import net.sourceforge.jpaxjc.ns.persistence.orm.NamedQuery;
import net.sourceforge.jpaxjc.ns.persistence.orm.OneToMany;
import net.sourceforge.jpaxjc.ns.persistence.orm.OneToOne;
import net.sourceforge.jpaxjc.ns.persistence.orm.PersistenceUnitMetadata;
import net.sourceforge.jpaxjc.ns.persistence.orm.PostLoad;
import net.sourceforge.jpaxjc.ns.persistence.orm.PostPersist;
import net.sourceforge.jpaxjc.ns.persistence.orm.PostRemove;
import net.sourceforge.jpaxjc.ns.persistence.orm.PostUpdate;
import net.sourceforge.jpaxjc.ns.persistence.orm.PrePersist;
import net.sourceforge.jpaxjc.ns.persistence.orm.PreRemove;
import net.sourceforge.jpaxjc.ns.persistence.orm.PreUpdate;
import net.sourceforge.jpaxjc.ns.persistence.orm.PrimaryKeyJoinColumn;
import net.sourceforge.jpaxjc.ns.persistence.orm.QueryHint;
import net.sourceforge.jpaxjc.ns.persistence.orm.SecondaryTable;
import net.sourceforge.jpaxjc.ns.persistence.orm.SequenceGenerator;
import net.sourceforge.jpaxjc.ns.persistence.orm.SqlResultSetMapping;
import net.sourceforge.jpaxjc.ns.persistence.orm.Table;
import net.sourceforge.jpaxjc.ns.persistence.orm.TableGenerator;
import net.sourceforge.jpaxjc.ns.persistence.orm.TemporalType;
import net.sourceforge.jpaxjc.ns.persistence.orm.Transient;
import net.sourceforge.jpaxjc.ns.persistence.orm.UniqueConstraint;
import net.sourceforge.jpaxjc.ns.persistence.orm.Version;
import com.sun.tools.xjc.BadCommandLineException;
import com.sun.tools.xjc.Options;
import com.sun.tools.xjc.Plugin;
import com.sun.tools.xjc.model.CCustomizations;
import com.sun.tools.xjc.model.CEnumLeafInfo;
import com.sun.tools.xjc.model.CPluginCustomization;
import com.sun.tools.xjc.model.CPropertyInfo;
import com.sun.tools.xjc.model.CTypeInfo;
import com.sun.tools.xjc.outline.Aspect;
import com.sun.tools.xjc.outline.ClassOutline;
import com.sun.tools.xjc.outline.FieldOutline;
import com.sun.tools.xjc.outline.Outline;
import com.sun.tools.xjc.outline.PackageOutline;
import com.sun.xml.xsom.XSAttributeDecl;
import com.sun.xml.xsom.XSAttributeUse;
import com.sun.xml.xsom.XSComponent;
import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSFacet;
import com.sun.xml.xsom.XSParticle;
import com.sun.xml.xsom.XSSimpleType;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXB;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.transform.dom.DOMSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * JPA-XJC plugin implementation.
 *
 * @author <a href="mailto:cs@schulte.it">Christian Schulte</a>
 * @version $Id$
 */
public final class PluginImpl extends Plugin
{
    // This class was written in about 2 hours. Don't expect any beauties!

    /** ORM version the plugin supports. */
    private static final String ORM_VERSION = "1.0";

    /** Persistence version the plugin supports. */
    private static final String PERSISTENCE_VERSION = "1.0";

    /** {@code http://java.sun.com/xml/ns/persistence} namespace URI. */
    private static final String PERSISTENCE_NS = "http://java.sun.com/xml/ns/persistence";

    /** Default {@code http://java.sun.com/xml/ns/persistence} schema system id. */
    private static final String DEFAULT_PERSISTENCE_SYSTEM_ID =
        "http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd";

    /** {@code http://java.sun.com/xml/ns/persistence} classpath location. */
    private static final String PERSISTENCE_SCHEMA_CLASSPATH_LOCATION =
        "/net/sourceforge/jpaxjc/ns/persistence/persistence_1_0.xsd";

    /** {@code http://java.sun.com/xml/ns/persistence/orm} namespace URI. */
    private static final String ORM_NS = "http://java.sun.com/xml/ns/persistence/orm";

    /** Default {@code http://java.sun.com/xml/ns/persistence/orm} schema system id. */
    private static final String DEFAULT_ORM_SYSTEM_ID = "http://java.sun.com/xml/ns/persistence/orm_1_0.xsd";

    /** {@code http://java.sun.com/xml/ns/persistence/orm} classpath location. */
    private static final String ORM_SCHEMA_CLASSPATH_LOCATION =
        "/net/sourceforge/jpaxjc/ns/persistence/orm/orm_1_0.xsd";

    /** Prefix added to messages logged to the console. */
    private static final String MESSAGE_PREFIX = "JPA-XJC";

    /** Constant for the {@code -jpa} option. */
    private static final String JPA_OPTION_NAME = "-jpa";

    /** Constant for the {@code -persistenceSystemId} option. */
    private static final String PERSISTENCE_SYSTEM_ID_OPTION_NAME = "-persistenceSystemId";

    /** Constant for the {@code -ormSystemId} option. */
    private static final String ORM_SYSTEM_ID_OPTION_NAME = "-ormSystemId";

    /** The persistence unit name of the generated unit. */
    private String persistenceUnitName;

    /** The persistence unit root of the generated unit. */
    private File persistenceUnitRoot;

    /** Options passed to the plugin. */
    private Options options;

    /** {@code http://java.sun.com/xml/ns/persistence} schema system id. */
    private String persistenceSystemId = DEFAULT_PERSISTENCE_SYSTEM_ID;

    /** {@code http://java.sun.com/xml/ns/persistence/orm} schema system id. */
    private String ormSystemId = DEFAULT_ORM_SYSTEM_ID;

    /** Set of mapped classes. */
    private final Set<String> mappedClasses = new HashSet<String>();

    /** Empty JType array. */
    private static final JType[] NO_JTYPES =
    {
    };

    // --
    // Plugin methods.
    // --
    @Override
    public String getOptionName()
    {
        return "jpa";
    }

    @Override
    public int parseArgument( final Options opt, final String[] args, int i )
        throws BadCommandLineException, IOException
    {
        if ( JPA_OPTION_NAME.equals( args[i] ) )
        {
            this.persistenceUnitName = opt.requireArgument( JPA_OPTION_NAME, args, ++i );
            this.persistenceUnitRoot = new File( opt.requireArgument( JPA_OPTION_NAME, args, ++i ) );
            return 3;
        }

        if ( PERSISTENCE_SYSTEM_ID_OPTION_NAME.equals( args[i] ) )
        {
            this.persistenceSystemId = opt.requireArgument( PERSISTENCE_SYSTEM_ID_OPTION_NAME, args, ++i );
            return 2;
        }

        if ( ORM_SYSTEM_ID_OPTION_NAME.equals( args[i] ) )
        {
            this.ormSystemId = opt.requireArgument( ORM_SYSTEM_ID_OPTION_NAME, args, ++i );
            return 2;
        }

        return 0;
    }

    @Override
    public String getUsage()
    {
        final String lineSeparator = System.getProperty( "line.separator" );
        final StringBuilder b = new StringBuilder();

        b.append( "  -jpa               :  " ).append( getMessage( "usage" ) ).append( lineSeparator );
        b.append( "                     :  " ).append( getMessage( "usageUnitName" ) ).append( lineSeparator );
        b.append( "                     :  " ).append( getMessage( "usageUnitRoot" ) ).append( lineSeparator );
        b.append( "                     :  " ).append( getMessage( "jpaSystemId", this.persistenceSystemId ) ).
            append( lineSeparator );

        b.append( "                     :  " ).append( getMessage( "ormSystemId", this.ormSystemId ) );

        return b.toString();
    }

    @Override
    public List<String> getCustomizationURIs()
    {
        return Arrays.asList( new String[]
            {
                ORM_NS, PERSISTENCE_NS
            } );

    }

    @Override
    public boolean isCustomizationTagName( final String nsUri, final String localName )
    {
        return nsUri.equals( ORM_NS ) || nsUri.equals( PERSISTENCE_NS );
    }

    @Override
    public boolean run( final Outline model, final Options options, final ErrorHandler errorHandler )
        throws SAXException
    {
        boolean success = true;
        this.options = options;

        this.log( Level.INFO, "title" );

        try
        {
            final EntityMappings orm = new EntityMappings();
            orm.setVersion( ORM_VERSION );

            this.mappedClasses.clear();

            for ( ClassOutline c : model.getClasses() )
            {
                this.toMappedSuperclass( model, c, orm );
            }
            for ( ClassOutline c : model.getClasses() )
            {
                this.toEmbeddable( c, orm );
            }
            for ( ClassOutline c : model.getClasses() )
            {
                this.toEntity( model, c, orm );
            }

            this.customizeOrm( model.getModel().getCustomizations(), orm );
            for ( PackageOutline p : model.getAllPackageContexts() )
            {
                this.generateAdapterMethods( model.getCodeModel(), p );
            }

            for ( Entity e : orm.getEntity() )
            {
                if ( e.getAttributes() == null )
                {
                    e.setAttributes( new Attributes() );
                }

                this.addMandatoryAttributes( orm, e.getAttributes(), this.getClassOutline( model, e.getClazz() ) );
            }

            final Persistence p = new Persistence();
            final Persistence.PersistenceUnit u = new Persistence.PersistenceUnit();
            u.setName( this.persistenceUnitName );
            p.setVersion( PERSISTENCE_VERSION );
            p.getPersistenceUnit().add( u );

            this.customizePersistenceUnit( model.getModel().getCustomizations(), u );

            for ( Entity e : orm.getEntity() )
            {
                if ( !u.getClazz().contains( e.getClazz() ) )
                {
                    u.getClazz().add( e.getClazz() );
                }
            }
            for ( Embeddable e : orm.getEmbeddable() )
            {
                if ( !u.getClazz().contains( e.getClazz() ) )
                {
                    u.getClazz().add( e.getClazz() );
                }
            }
            for ( MappedSuperclass m : orm.getMappedSuperclass() )
            {
                if ( !u.getClazz().contains( m.getClazz() ) )
                {
                    u.getClazz().add( m.getClazz() );
                }
            }

            this.annotate( model, orm );
            orm.getEmbeddable().clear();
            orm.getEntity().clear();
            orm.getMappedSuperclass().clear();

            final SchemaFactory schemaFactory = SchemaFactory.newInstance( XMLConstants.W3C_XML_SCHEMA_NS_URI );
            final Schema persistenceSchema =
                schemaFactory.newSchema( this.getClass().getResource( PERSISTENCE_SCHEMA_CLASSPATH_LOCATION ) );

            final Schema ormSchema =
                schemaFactory.newSchema( this.getClass().getResource( ORM_SCHEMA_CLASSPATH_LOCATION ) );

            final JAXBContext persistenceCtx = JAXBContext.newInstance( "net.sourceforge.jpaxjc.ns.persistence" );
            final JAXBContext ormCtx = JAXBContext.newInstance( "net.sourceforge.jpaxjc.ns.persistence.orm" );
            final Marshaller persistenceMarshaller = persistenceCtx.createMarshaller();
            final Marshaller ormMarshaller = ormCtx.createMarshaller();
            persistenceMarshaller.setSchema( persistenceSchema );
            persistenceMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            persistenceMarshaller.setProperty( Marshaller.JAXB_SCHEMA_LOCATION,
                                               PERSISTENCE_NS + ' ' + this.persistenceSystemId );

            ormMarshaller.setSchema( ormSchema );
            ormMarshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
            ormMarshaller.setProperty( Marshaller.JAXB_SCHEMA_LOCATION, ORM_NS + ' ' + this.ormSystemId );

            final File metaInf = new File( this.persistenceUnitRoot, "META-INF" );
            if ( !metaInf.exists() )
            {
                success = metaInf.mkdirs();
            }

            if ( orm.getAccess() != null
                 || orm.getCatalog() != null
                 || orm.getDescription() != null
                 || !orm.getNamedNativeQuery().isEmpty()
                 || !orm.getNamedQuery().isEmpty()
                 || orm.getPackage() != null
                 || orm.getPersistenceUnitMetadata() != null
                 || orm.getSchema() != null
                 || !orm.getSequenceGenerator().isEmpty()
                 || !orm.getSqlResultSetMapping().isEmpty()
                 || !orm.getTableGenerator().isEmpty()
                 || !orm.getEmbeddable().isEmpty()
                 || !orm.getEntity().isEmpty()
                 || !orm.getMappedSuperclass().isEmpty() )
            {
                final File ormFile = new File( metaInf, this.persistenceUnitName + ".xml" );
                this.log( Level.INFO, "writing", ormFile.getAbsolutePath() );

                ormMarshaller.marshal( orm, ormFile );
                u.getMappingFile().add( "META-INF/" + this.persistenceUnitName + ".xml" );
            }

            final File persistenceFile = new File( metaInf, "persistence.xml" );
            this.log( Level.INFO, "writing", persistenceFile.getAbsolutePath() );

            persistenceMarshaller.marshal( p, persistenceFile );
        }
        catch ( JAXBException e )
        {
            errorHandler.fatalError( new SAXParseException( e.getMessage(), null, e ) );
            success = false;
        }

        return success;
    }

    // ---
    // Mapping methods.
    // ---
    private void toMappedSuperclass( final Outline outline, final ClassOutline c, final EntityMappings orm )
        throws JAXBException
    {
        if ( !this.mappedClasses.contains( c.implClass.binaryName() )
             && c.target.getCustomizations().find( ORM_NS, "mapped-superclass" ) != null )
        {
            final CPluginCustomization pc = c.target.getCustomizations().find( ORM_NS, "mapped-superclass" );
            final MappedSuperclass mappedSuperclass =
                JAXB.unmarshal( new DOMSource( pc.element ), MappedSuperclass.class );

            orm.getMappedSuperclass().add( mappedSuperclass );
            this.toMappedSuperclass( outline, c, orm, mappedSuperclass );

            if ( !pc.isAcknowledged() )
            {
                pc.markAsAcknowledged();
            }

            this.mappedClasses.add( mappedSuperclass.getClazz() );
        }
    }

    private void toEmbeddable( final ClassOutline c, final EntityMappings orm )
        throws JAXBException
    {
        if ( !this.mappedClasses.contains( c.implClass.binaryName() )
             && c.target.getCustomizations().find( ORM_NS, "embeddable" ) != null )
        {
            final CPluginCustomization pc = c.target.getCustomizations().find( ORM_NS, "embeddable" );
            final Embeddable embeddable = JAXB.unmarshal( new DOMSource( pc.element ), Embeddable.class );

            orm.getEmbeddable().add( embeddable );
            this.toEmbeddable( c, embeddable );

            if ( !pc.isAcknowledged() )
            {
                pc.markAsAcknowledged();
            }

            this.mappedClasses.add( embeddable.getClazz() );
        }
    }

    private void toEntity( final Outline outline, final ClassOutline c, final EntityMappings orm )
        throws JAXBException
    {
        if ( !this.mappedClasses.contains( c.implClass.binaryName() ) )
        {
            boolean mapped = false;

            Entity entity = null;
            if ( c.target.getCustomizations().find( ORM_NS, "entity" ) != null )
            {
                final CPluginCustomization pc = c.target.getCustomizations().find( ORM_NS, "entity" );
                entity = JAXB.unmarshal( new DOMSource( pc.element ), Entity.class );

                orm.getEntity().add( entity );
                this.toEntity( outline, c, entity );

                mapped = true;

                if ( !pc.isAcknowledged() )
                {
                    pc.markAsAcknowledged();
                }
            }

            if ( !mapped )
            {
                // Defaults to create an entity per class.
                entity = new Entity();
                String name = c.implClass.binaryName();
                entity.setClazz( name );

                final String pkgName = c.implClass.getPackage().name();
                if ( pkgName != null && pkgName.length() > 0 )
                {
                    name = name.substring( pkgName.length() + 1 );
                }

                entity.setName( name );
                orm.getEntity().add( entity );
                this.toEntity( outline, c, entity );
                mapped = true;
            }

            this.mappedClasses.add( entity.getClazz() );
        }
    }

    private void toEntity( final Outline outline, final ClassOutline c, final Entity entity )
        throws JAXBException
    {
        if ( entity.getAccess() == null )
        {
            entity.setAccess( AccessType.PROPERTY );
        }
        if ( entity.getClazz() == null )
        {
            entity.setClazz( c.implClass.binaryName() );
        }
        if ( entity.getName() == null )
        {
            String name = c.implClass.binaryName();
            String pkgName = c.implClass.getPackage().name();

            if ( pkgName != null && pkgName.length() > 0 )
            {
                name = name.substring( pkgName.length() + 1 );
            }

            entity.setName( name );
        }
        if ( entity.getAttributes() == null )
        {
            entity.setAttributes( new Attributes() );
        }

        this.toAttributes( outline, c, entity.getAttributes() );

        this.customizeEntity( c.target.getCustomizations(), entity );
    }

    private void toEmbeddable( final ClassOutline c, final Embeddable embeddable )
        throws JAXBException
    {
        if ( embeddable.getAccess() == null )
        {
            embeddable.setAccess( AccessType.PROPERTY );
        }
        if ( embeddable.getClazz() == null )
        {
            embeddable.setClazz( c.implClass.binaryName() );
        }
        if ( embeddable.getAttributes() == null )
        {
            embeddable.setAttributes( new EmbeddableAttributes() );
        }

        this.toEmbeddableAttributes( c, embeddable.getAttributes() );

        this.customizeEmbeddable( c.target.getCustomizations(), embeddable );
    }

    private void toMappedSuperclass( final Outline outline, final ClassOutline c, final EntityMappings orm,
                                     final MappedSuperclass mappedSuperclass )
        throws JAXBException
    {
        if ( mappedSuperclass.getAccess() == null )
        {
            mappedSuperclass.setAccess( AccessType.PROPERTY );
        }
        if ( mappedSuperclass.getClazz() == null )
        {
            mappedSuperclass.setClazz( c.implClass.binaryName() );
        }
        if ( mappedSuperclass.getAttributes() == null )
        {
            mappedSuperclass.setAttributes( new Attributes() );
        }

        this.toAttributes( outline, c, mappedSuperclass.getAttributes() );

        this.customizeMappedSuperclass( c.target.getCustomizations(), mappedSuperclass );
    }

    private void toAttributes( final Outline outline, final ClassOutline c, final Attributes attributes )
        throws JAXBException
    {
        if ( c.target.declaresAttributeWildcard() )
        {
            final Transient t = new Transient();
            t.setName( "otherAttributes" );
            attributes.getTransient().add( t );

            this.log( Level.WARNING, "cannotMapProperty", "OtherAttributes", c.implClass.binaryName() );
        }

        for ( FieldOutline f : c.getDeclaredFields() )
        {
            boolean mapped = false;

            if ( f.getPropertyInfo().getCustomizations().find( ORM_NS, "id" ) != null )
            {
                final CPluginCustomization pc = f.getPropertyInfo().getCustomizations().find( ORM_NS, "id" );
                final Id id = JAXB.unmarshal( new DOMSource( pc.element ), Id.class );

                if ( id.getName() == null )
                {
                    id.setName( f.getPropertyInfo().getName( false ) );
                }

                attributes.getId().add( id );
                mapped = true;

                final Column defaultColumn =
                    this.applySchemaDefaults( f.getPropertyInfo().getSchemaComponent(), id.getColumn() );

                id.setColumn( defaultColumn );

                if ( !pc.isAcknowledged() )
                {
                    pc.markAsAcknowledged();
                }
            }

            if ( f.getPropertyInfo().getCustomizations().find( ORM_NS, "basic" ) != null )
            {
                final CPluginCustomization pc = f.getPropertyInfo().getCustomizations().find( ORM_NS, "basic" );
                final Basic basic = JAXB.unmarshal( new DOMSource( pc.element ), Basic.class );

                if ( basic.getName() == null )
                {
                    basic.setName( f.getPropertyInfo().getName( false ) );
                }

                attributes.getBasic().add( basic );
                mapped = true;

                if ( !pc.isAcknowledged() )
                {
                    pc.markAsAcknowledged();
                }

                final Column defaultColumn =
                    this.applySchemaDefaults( f.getPropertyInfo().getSchemaComponent(), basic.getColumn() );

                basic.setColumn( defaultColumn );
            }

            if ( f.getPropertyInfo().getCustomizations().find( ORM_NS, "version" ) != null )
            {
                final CPluginCustomization pc = f.getPropertyInfo().getCustomizations().find( ORM_NS, "version" );
                final Version version = JAXB.unmarshal( new DOMSource( pc.element ), Version.class );

                if ( version.getName() == null )
                {
                    version.setName( f.getPropertyInfo().getName( false ) );
                }

                attributes.getVersion().add( version );
                mapped = true;

                if ( !pc.isAcknowledged() )
                {
                    pc.markAsAcknowledged();
                }

                final Column defaultColumn =
                    this.applySchemaDefaults( f.getPropertyInfo().getSchemaComponent(), version.getColumn() );

                version.setColumn( defaultColumn );
            }

            if ( f.getPropertyInfo().getCustomizations().find( ORM_NS, "many-to-one" ) != null )
            {
                final CPluginCustomization pc = f.getPropertyInfo().getCustomizations().find( ORM_NS, "many-to-one" );
                final ManyToOne m = JAXB.unmarshal( new DOMSource( pc.element ), ManyToOne.class );

                if ( m.getName() == null )
                {
                    m.setName( f.getPropertyInfo().getName( false ) );
                }

                attributes.getManyToOne().add( m );
                mapped = true;

                if ( !pc.isAcknowledged() )
                {
                    pc.markAsAcknowledged();
                }
            }

            if ( f.getPropertyInfo().getCustomizations().find( ORM_NS, "one-to-many" ) != null )
            {
                final CPluginCustomization pc = f.getPropertyInfo().getCustomizations().find( ORM_NS, "one-to-many" );
                final OneToMany o = JAXB.unmarshal( new DOMSource( pc.element ), OneToMany.class );

                if ( o.getName() == null )
                {
                    o.setName( f.getPropertyInfo().getName( false ) );
                }

                attributes.getOneToMany().add( o );

                this.generateCollectionSetter( f.parent().parent().getCodeModel(), f.parent(), f.getPropertyInfo() );
                mapped = true;

                if ( !pc.isAcknowledged() )
                {
                    pc.markAsAcknowledged();
                }
            }

            if ( f.getPropertyInfo().getCustomizations().find( ORM_NS, "one-to-one" ) != null )
            {
                final CPluginCustomization pc = f.getPropertyInfo().getCustomizations().find( ORM_NS, "one-to-one" );
                final OneToOne o = JAXB.unmarshal( new DOMSource( pc.element ), OneToOne.class );

                if ( o.getName() == null )
                {
                    o.setName( f.getPropertyInfo().getName( false ) );
                }

                attributes.getOneToOne().add( o );
                mapped = true;

                if ( !pc.isAcknowledged() )
                {
                    pc.markAsAcknowledged();
                }
            }

            if ( f.getPropertyInfo().getCustomizations().find( ORM_NS, "many-to-many" ) != null )
            {
                final CPluginCustomization pc = f.getPropertyInfo().getCustomizations().find( ORM_NS, "many-to-many" );
                final ManyToMany m = JAXB.unmarshal( new DOMSource( pc.element ), ManyToMany.class );

                if ( m.getName() == null )
                {
                    m.setName( f.getPropertyInfo().getName( false ) );
                }

                attributes.getManyToMany().add( m );

                this.generateCollectionSetter( f.parent().parent().getCodeModel(), f.parent(), f.getPropertyInfo() );
                mapped = true;

                if ( !pc.isAcknowledged() )
                {
                    pc.markAsAcknowledged();
                }
            }

            if ( f.getPropertyInfo().getCustomizations().find( ORM_NS, "embedded" ) != null )
            {
                final CPluginCustomization pc = f.getPropertyInfo().getCustomizations().find( ORM_NS, "embedded" );
                final Embedded e = JAXB.unmarshal( new DOMSource( pc.element ), Embedded.class );

                if ( e.getName() == null )
                {
                    e.setName( f.getPropertyInfo().getName( false ) );
                }

                attributes.getEmbedded().add( e );
                mapped = true;

                if ( !pc.isAcknowledged() )
                {
                    pc.markAsAcknowledged();
                }
            }

            if ( f.getPropertyInfo().getCustomizations().find( ORM_NS, "transient" ) != null )
            {
                final CPluginCustomization pc = f.getPropertyInfo().getCustomizations().find( ORM_NS, "transient" );
                final Transient t = JAXB.unmarshal( new DOMSource( pc.element ), Transient.class );

                if ( t.getName() == null )
                {
                    t.setName( f.getPropertyInfo().getName( false ) );
                }

                attributes.getTransient().add( t );
                mapped = true;

                if ( !pc.isAcknowledged() )
                {
                    pc.markAsAcknowledged();
                }
            }

            if ( !mapped )
            {
                this.toDefaultAttribute( outline, f, attributes );
            }
        }
    }

    private void toEmbeddableAttributes( final ClassOutline c, final EmbeddableAttributes attributes )
        throws JAXBException
    {
        if ( c.target.declaresAttributeWildcard() )
        {
            final Transient t = new Transient();
            t.setName( "otherAttributes" );
            attributes.getTransient().add( t );

            this.log( Level.WARNING, "cannotMapProperty", "OtherAttributes", c.implClass.binaryName() );
        }

        for ( FieldOutline f : c.getDeclaredFields() )
        {
            boolean mapped = false;

            if ( f.getPropertyInfo().getCustomizations().find( ORM_NS, "basic" ) != null )
            {
                final CPluginCustomization pc = f.getPropertyInfo().getCustomizations().find( ORM_NS, "basic" );
                final Basic basic = JAXB.unmarshal( new DOMSource( pc.element ), Basic.class );

                if ( basic.getName() == null )
                {
                    basic.setName( f.getPropertyInfo().getName( false ) );
                }

                attributes.getBasic().add( basic );
                mapped = true;

                if ( !pc.isAcknowledged() )
                {
                    pc.markAsAcknowledged();
                }
            }

            if ( f.getPropertyInfo().getCustomizations().find( ORM_NS, "transient" ) != null )
            {
                final CPluginCustomization pc = f.getPropertyInfo().getCustomizations().find( ORM_NS, "transient" );
                final Transient t = JAXB.unmarshal( new DOMSource( pc.element ), Transient.class );

                if ( t.getName() == null )
                {
                    t.setName( f.getPropertyInfo().getName( false ) );
                }

                attributes.getTransient().add( t );
                mapped = true;

                if ( !pc.isAcknowledged() )
                {
                    pc.markAsAcknowledged();
                }
            }

            if ( !mapped )
            {
                this.toDefaultAttribute( f, attributes );
            }
        }
    }

    private boolean toDefaultAttribute( final Outline outline, final FieldOutline f, final Attributes a )
    {
        boolean mapped = false;

        if ( !mapped && this.isBasicFieldOutline( f ) )
        {
            final Basic basic = new Basic();
            if ( f.getPropertyInfo().isOptionalPrimitive() )
            {
                basic.setOptional( true );
            }

            basic.setName( f.getPropertyInfo().getName( false ) );
            basic.setColumn( this.toColumn( f.getPropertyInfo().getSchemaComponent() ) );
            this.toTemporal( f, basic );
            a.getBasic().add( basic );
            mapped = true;
        }

        if ( !mapped )
        {
            final Collection<? extends CTypeInfo> ref = f.getPropertyInfo().ref();

            if ( ref != null && ref.size() == 1 )
            {
                final CTypeInfo refType = ref.iterator().next();
                final ClassOutline refClass =
                    this.getClassOutline( outline, refType.toType( outline, Aspect.EXPOSED ).binaryName() );

                if ( refClass != null )
                {
                    if ( f.getPropertyInfo().isCollection() )
                    {
                        final OneToMany m = new OneToMany();
                        m.setName( f.getPropertyInfo().getName( false ) );
                        a.getOneToMany().add( m );

                        this.generateCollectionSetter( outline.getCodeModel(), f.parent(), f.getPropertyInfo() );
                        mapped = true;
                    }

                    if ( !mapped )
                    {
                        final OneToOne o = new OneToOne();
                        o.setName( f.getPropertyInfo().getName( false ) );
                        a.getOneToOne().add( o );

                        final Column col = this.toColumn( f.getPropertyInfo().getSchemaComponent() );
                        o.setOptional( col != null && col.isNullable() );

                        mapped = true;
                    }
                }
            }
        }

        if ( !mapped )
        {
            mapped = this.toTransient( f, a );
        }

        return mapped;
    }

    private boolean toDefaultAttribute( final FieldOutline f, final EmbeddableAttributes a )
    {
        boolean mapped = false;

        if ( !mapped && this.isBasicFieldOutline( f ) )
        {
            final Basic basic = new Basic();
            if ( f.getPropertyInfo().isOptionalPrimitive() )
            {
                basic.setOptional( true );
            }

            basic.setName( f.getPropertyInfo().getName( false ) );
            basic.setColumn( this.toColumn( f.getPropertyInfo().getSchemaComponent() ) );
            this.toTemporal( f, basic );
            a.getBasic().add( basic );
            mapped = true;
        }

        if ( !mapped )
        {
            mapped = this.toTransient( f, a );
        }

        return mapped;
    }

    private void toTemporal( final FieldOutline f, final Basic basic )
    {
        final String typeName = f.getRawType().binaryName();
        final JCodeModel cm = f.parent().parent().getCodeModel();

        if ( typeName.equals( cm.ref( java.util.Date.class ).binaryName() )
             || typeName.equals( cm.ref( java.sql.Date.class ).binaryName() )
             || typeName.equals( cm.ref( Calendar.class ).binaryName() ) )
        {
            basic.setTemporal( TemporalType.DATE );
        }
        else if ( typeName.equals( cm.ref( java.sql.Time.class ).binaryName() ) )
        {
            basic.setTemporal( TemporalType.TIME );
        }
        else if ( typeName.equals( cm.ref( java.sql.Timestamp.class ).binaryName() ) )
        {
            basic.setTemporal( TemporalType.TIMESTAMP );
        }
    }

    private void addMandatoryAttributes( final EntityMappings orm, final Attributes a, final ClassOutline c )
    {
        class RecursionHelper
        {

            void recurseAddId( final EntityMappings orm, final Attributes a, final ClassOutline c )
            {
                if ( a.getId().isEmpty() )
                {
                    if ( c.getSuperClass() != null )
                    {
                        final Attributes attr = getAttributes( orm, c.getSuperClass().implClass.binaryName() );
                        recurseAddId( orm, attr, c.getSuperClass() );
                    }
                    else
                    {
                        final Id id = new Id();
                        final GeneratedValue gv = new GeneratedValue();
                        final Column col = new Column();
                        a.getId().add( id );

                        gv.setStrategy( GenerationType.AUTO );
                        col.setScale( 0 );
                        col.setPrecision( 20 );
                        col.setNullable( false );
                        id.setName( "jpaId" );
                        id.setGeneratedValue( gv );
                        id.setColumn( col );

                        generateProperty( id.getName(), Long.TYPE, c );
                    }
                }
            }

            void recurseAddVersion( final EntityMappings orm, final Attributes a, final ClassOutline c )
            {
                if ( a.getVersion().isEmpty() )
                {
                    if ( c.getSuperClass() != null )
                    {
                        final Attributes attr = getAttributes( orm, c.getSuperClass().implClass.binaryName() );
                        if ( attr != null )
                        {
                            this.recurseAddVersion( orm, attr, c.getSuperClass() );
                        }
                    }
                    else
                    {
                        final Version v = new Version();
                        final Column col = new Column();
                        a.getVersion().add( v );

                        col.setScale( 0 );
                        col.setPrecision( 20 );
                        col.setNullable( false );
                        v.setName( "jpaVersion" );
                        v.setColumn( col );

                        generateProperty( v.getName(), Long.TYPE, c );
                    }
                }
            }

        }

        final RecursionHelper rh = new RecursionHelper();
        rh.recurseAddId( orm, a, c );
        rh.recurseAddVersion( orm, a, c );
    }

    private boolean toTransient( final FieldOutline f, final Attributes a )
    {
        final Transient t = new Transient();
        t.setName( f.getPropertyInfo().getName( false ) );
        a.getTransient().add( t );

        if ( f.getRawType().equals( f.parent().parent().getCodeModel().ref( XMLGregorianCalendar.class ) ) )
        {
            a.getBasic().add( this.toTemporalBasic( f ) );
        }
        else
        {
            this.log( Level.WARNING, "cannotMapProperty", f.getPropertyInfo().getName( true ),
                      f.parent().implClass.binaryName() );

        }

        return true;
    }

    private boolean toTransient( final FieldOutline f, final EmbeddableAttributes a )
    {
        final Transient t = new Transient();
        t.setName( f.getPropertyInfo().getName( false ) );
        a.getTransient().add( t );

        if ( f.getRawType().equals( f.parent().parent().getCodeModel().ref( XMLGregorianCalendar.class ) ) )
        {
            a.getBasic().add( this.toTemporalBasic( f ) );
        }
        else
        {
            this.log( Level.WARNING, "cannotMapProperty", f.getPropertyInfo().getName( true ),
                      f.parent().implClass.binaryName() );

        }

        return true;
    }

    private Basic toTemporalBasic( final FieldOutline f )
    {
        final Basic b = new Basic();
        b.setName( "jpa" + f.getPropertyInfo().getName( true ) );
        b.setTemporal( TemporalType.TIMESTAMP );

        this.generateTemporalBasic( f );

        return b;
    }

    private Column toColumn( final XSComponent xs )
    {
        Column col = null;
        boolean columnEmpty = true;

        XSSimpleType type = null;
        if ( xs instanceof XSParticle )
        {
            col = new Column();
            col.setNullable( ( (XSParticle) xs ).getMinOccurs() == 0 );
            columnEmpty = false;
        }
        else if ( xs instanceof XSSimpleType )
        {
            col = new Column();
            type = (XSSimpleType) xs;
        }
        else if ( xs instanceof XSAttributeDecl )
        {
            col = new Column();
            type = ( (XSAttributeDecl) xs ).getType();
        }
        else if ( xs instanceof XSAttributeUse )
        {
            final XSAttributeUse au = (XSAttributeUse) xs;
            col = new Column();
            col.setNullable( !au.isRequired() );
            columnEmpty = false;
            type = ( (XSAttributeUse) xs ).getDecl().getType();
        }
        else if ( xs instanceof XSElementDecl )
        {
            col = new Column();
            final XSElementDecl decl = (XSElementDecl) xs;
            col.setNullable( decl.isNillable() );
            columnEmpty = false;
            type = ( (XSElementDecl) xs ).getType().asSimpleType();
        }

        if ( type != null )
        {
            final XSFacet length = type.getFacet( XSFacet.FACET_LENGTH );
            final XSFacet maxLength = type.getFacet( XSFacet.FACET_MAXLENGTH );
            final XSFacet fractionDigits = type.getFacet( XSFacet.FACET_FRACTIONDIGITS );
            final XSFacet totalDigits = type.getFacet( XSFacet.FACET_TOTALDIGITS );

            if ( length != null )
            {
                col.setLength( new Integer( length.getValue().value ) );
                columnEmpty = false;
            }
            else if ( maxLength != null )
            {
                col.setLength( new Integer( maxLength.getValue().value ) );
                columnEmpty = false;
            }

            if ( fractionDigits != null )
            {
                col.setScale( new Integer( fractionDigits.getValue().value ) );
                columnEmpty = false;
            }
            if ( totalDigits != null )
            {
                col.setPrecision( new Integer( totalDigits.getValue().value ) );
                columnEmpty = false;
            }

            if ( this.getSchemaSimpleType( type, "decimal" ) != null
                 && ( col.getScale() == null || col.getPrecision() == null ) )
            {
                XSSimpleType schemaType = this.getSchemaSimpleType( type, "integer" );
                if ( schemaType != null && col.getScale() == null )
                {
                    col.setScale( new Integer( 0 ) );
                    columnEmpty = false;
                }

                if ( col.getPrecision() == null )
                {
                    schemaType = this.getSchemaSimpleType( type, "long" );
                    if ( schemaType != null )
                    {
                        col.setPrecision( new Integer( 20 ) );
                        columnEmpty = false;
                    }
                    schemaType = this.getSchemaSimpleType( type, "int" );
                    if ( schemaType != null )
                    {
                        col.setPrecision( new Integer( 10 ) );
                        columnEmpty = false;
                    }
                    schemaType = this.getSchemaSimpleType( type, "short" );
                    if ( schemaType != null )
                    {
                        col.setPrecision( new Integer( 5 ) );
                        columnEmpty = false;
                    }
                    schemaType = this.getSchemaSimpleType( type, "byte" );
                    if ( schemaType != null )
                    {
                        col.setPrecision( new Integer( 3 ) );
                        columnEmpty = false;
                    }
                    schemaType = this.getSchemaSimpleType( type, "unsignedLong" );
                    if ( schemaType != null )
                    {
                        col.setPrecision( new Integer( 20 ) );
                        columnEmpty = false;
                    }
                    schemaType = this.getSchemaSimpleType( type, "unsignedInt" );
                    if ( schemaType != null )
                    {
                        col.setPrecision( new Integer( 10 ) );
                        columnEmpty = false;
                    }
                    schemaType = this.getSchemaSimpleType( type, "unsignedShort" );
                    if ( schemaType != null )
                    {
                        col.setPrecision( new Integer( 5 ) );
                        columnEmpty = false;
                    }
                    schemaType = this.getSchemaSimpleType( type, "unsignedByte" );
                    if ( schemaType != null )
                    {
                        col.setPrecision( new Integer( 3 ) );
                        columnEmpty = false;
                    }
                }
            }
        }

        return columnEmpty ? null : col;
    }

    private Column applySchemaDefaults( final XSComponent comp, final Column column )
    {
        final Column defaultColumn = this.toColumn( comp );
        if ( defaultColumn != null )
        {
            if ( column == null )
            {
                return defaultColumn;
            }

            if ( column.isInsertable() == null )
            {
                column.setInsertable( defaultColumn.isInsertable() );
            }
            if ( column.isNullable() == null )
            {
                column.setNullable( defaultColumn.isNullable() );
            }
            if ( column.isUnique() == null )
            {
                column.setUnique( defaultColumn.isUnique() );
            }
            if ( column.isUpdatable() == null )
            {
                column.setUpdatable( defaultColumn.isUpdatable() );
            }
            if ( column.getColumnDefinition() == null )
            {
                column.setColumnDefinition( defaultColumn.getColumnDefinition() );
            }
            if ( column.getLength() == null )
            {
                column.setLength( defaultColumn.getLength() );
            }
            if ( column.getName() == null )
            {
                column.setName( defaultColumn.getName() );
            }
            if ( column.getPrecision() == null )
            {
                column.setPrecision( defaultColumn.getPrecision() );
            }
            if ( column.getScale() == null )
            {
                column.setScale( defaultColumn.getScale() );
            }
            if ( column.getTable() == null )
            {
                column.setTable( defaultColumn.getTable() );
            }
        }

        return column;
    }

    // --
    // Customization methods.
    // --
    private void customizeOrm( final CCustomizations customizations, final EntityMappings orm )
    {
        for ( CPluginCustomization c : customizations )
        {
            if ( c.element != null && c.element.getNamespaceURI().equals( ORM_NS ) )
            {
                boolean acknowledge = false;

                if ( c.element.getLocalName().equals( "description" ) )
                {
                    orm.setDescription( c.element.getFirstChild().getNodeValue() );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "persistence-unit-metadata" ) )
                {
                    final PersistenceUnitMetadata e =
                        JAXB.unmarshal( new DOMSource( c.element ), PersistenceUnitMetadata.class );

                    orm.setPersistenceUnitMetadata( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "package" ) )
                {
                    orm.setPackage( c.element.getFirstChild().getNodeValue() );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "schema" ) )
                {
                    orm.setSchema( c.element.getFirstChild().getNodeValue() );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "catalog" ) )
                {
                    orm.setCatalog( c.element.getFirstChild().getNodeValue() );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "access" ) )
                {
                    final AccessType e = JAXB.unmarshal( new DOMSource( c.element ), AccessType.class );
                    orm.setAccess( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "sequence-generator" ) )
                {
                    final SequenceGenerator e = JAXB.unmarshal( new DOMSource( c.element ), SequenceGenerator.class );
                    orm.getSequenceGenerator().add( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "table-generator" ) )
                {
                    final TableGenerator e = JAXB.unmarshal( new DOMSource( c.element ), TableGenerator.class );
                    orm.getTableGenerator().add( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "named-query" ) )
                {
                    final NamedQuery e = JAXB.unmarshal( new DOMSource( c.element ), NamedQuery.class );
                    orm.getNamedQuery().add( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "named-native-query" ) )
                {
                    final NamedNativeQuery e = JAXB.unmarshal( new DOMSource( c.element ), NamedNativeQuery.class );
                    orm.getNamedNativeQuery().add( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "sql-result-set-mapping" ) )
                {
                    final SqlResultSetMapping e =
                        JAXB.unmarshal( new DOMSource( c.element ), SqlResultSetMapping.class );

                    orm.getSqlResultSetMapping().add( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "mapped-superclass" ) )
                {
                    final MappedSuperclass e = JAXB.unmarshal( new DOMSource( c.element ), MappedSuperclass.class );
                    orm.getMappedSuperclass().add( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "entity" ) )
                {
                    final Entity e = JAXB.unmarshal( new DOMSource( c.element ), Entity.class );
                    orm.getEntity().add( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "embeddable" ) )
                {
                    final Embeddable e = JAXB.unmarshal( new DOMSource( c.element ), Embeddable.class );
                    orm.getEmbeddable().add( e );
                    acknowledge = true;
                }

                if ( acknowledge && !c.isAcknowledged() )
                {
                    c.markAsAcknowledged();
                }
            }
        }
    }

    private void customizeEntity( final CCustomizations customizations, final Entity entity )
    {
        for ( CPluginCustomization c : customizations )
        {
            if ( c.element != null && c.element.getNamespaceURI().equals( ORM_NS ) )
            {
                boolean acknowledge = false;

                if ( c.element.getLocalName().equals( "description" ) )
                {
                    entity.setDescription( c.element.getFirstChild().getNodeValue() );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "table" ) )
                {
                    final Table e = JAXB.unmarshal( new DOMSource( c.element ), Table.class );
                    entity.setTable( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "secondary-table" ) )
                {
                    final SecondaryTable e = JAXB.unmarshal( new DOMSource( c.element ), SecondaryTable.class );
                    entity.getSecondaryTable().add( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "primary-key-join-column" ) )
                {
                    final PrimaryKeyJoinColumn e =
                        JAXB.unmarshal( new DOMSource( c.element ), PrimaryKeyJoinColumn.class );

                    entity.getPrimaryKeyJoinColumn().add( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "id-class" ) )
                {
                    final IdClass e = JAXB.unmarshal( new DOMSource( c.element ), IdClass.class );
                    entity.setIdClass( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "inheritance" ) )
                {
                    final Inheritance e = JAXB.unmarshal( new DOMSource( c.element ), Inheritance.class );
                    entity.setInheritance( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "discriminator-value" ) )
                {
                    entity.setDiscriminatorValue( c.element.getFirstChild().getNodeValue() );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "discriminator-column" ) )
                {
                    final DiscriminatorColumn e =
                        JAXB.unmarshal( new DOMSource( c.element ), DiscriminatorColumn.class );

                    entity.setDiscriminatorColumn( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "sequence-generator" ) )
                {
                    final SequenceGenerator e = JAXB.unmarshal( new DOMSource( c.element ), SequenceGenerator.class );
                    entity.setSequenceGenerator( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "table-generator" ) )
                {
                    final TableGenerator e = JAXB.unmarshal( new DOMSource( c.element ), TableGenerator.class );
                    entity.setTableGenerator( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "named-query" ) )
                {
                    final NamedQuery e = JAXB.unmarshal( new DOMSource( c.element ), NamedQuery.class );
                    entity.getNamedQuery().add( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "named-native-query" ) )
                {
                    final NamedNativeQuery e = JAXB.unmarshal( new DOMSource( c.element ), NamedNativeQuery.class );
                    entity.getNamedNativeQuery().add( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "sql-result-set-mapping" ) )
                {
                    final SqlResultSetMapping e =
                        JAXB.unmarshal( new DOMSource( c.element ), SqlResultSetMapping.class );

                    entity.getSqlResultSetMapping().add( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "exclude-default-listeners" ) )
                {
                    final EmptyType e = JAXB.unmarshal( new DOMSource( c.element ), EmptyType.class );
                    entity.setExcludeDefaultListeners( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "exclude-superclass-listeners" ) )
                {
                    final EmptyType e = JAXB.unmarshal( new DOMSource( c.element ), EmptyType.class );
                    entity.setExcludeSuperclassListeners( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "entity-listeners" ) )
                {
                    final EntityListeners e = JAXB.unmarshal( new DOMSource( c.element ), EntityListeners.class );
                    entity.setEntityListeners( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "pre-persist" ) )
                {
                    final PrePersist e = JAXB.unmarshal( new DOMSource( c.element ), PrePersist.class );
                    entity.setPrePersist( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "post-persist" ) )
                {
                    final PostPersist e = JAXB.unmarshal( new DOMSource( c.element ), PostPersist.class );
                    entity.setPostPersist( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "pre-remove" ) )
                {
                    final PreRemove e = JAXB.unmarshal( new DOMSource( c.element ), PreRemove.class );
                    entity.setPreRemove( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "post-remove" ) )
                {
                    final PostRemove e = JAXB.unmarshal( new DOMSource( c.element ), PostRemove.class );
                    entity.setPostRemove( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "pre-update" ) )
                {
                    final PreUpdate e = JAXB.unmarshal( new DOMSource( c.element ), PreUpdate.class );
                    entity.setPreUpdate( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "post-update" ) )
                {
                    final PostUpdate e = JAXB.unmarshal( new DOMSource( c.element ), PostUpdate.class );
                    entity.setPostUpdate( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "post-load" ) )
                {
                    final PostLoad e = JAXB.unmarshal( new DOMSource( c.element ), PostLoad.class );
                    entity.setPostLoad( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "attribute-override" ) )
                {
                    final AttributeOverride e = JAXB.unmarshal( new DOMSource( c.element ), AttributeOverride.class );
                    entity.getAttributeOverride().add( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "attributes" ) )
                {
                    final Attributes e = JAXB.unmarshal( new DOMSource( c.element ), Attributes.class );
                    entity.setAttributes( e );
                    acknowledge = true;
                }

                if ( acknowledge && !c.isAcknowledged() )
                {
                    c.markAsAcknowledged();
                }
            }
        }
    }

    private void customizeEmbeddable( final CCustomizations customizations, final Embeddable embeddable )
    {
        for ( CPluginCustomization c : customizations )
        {
            if ( c.element != null && c.element.getNamespaceURI().equals( ORM_NS ) )
            {
                boolean acknowledge = false;

                if ( c.element.getLocalName().equals( "description" ) )
                {
                    embeddable.setDescription( c.element.getFirstChild().getNodeValue() );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "attributes" ) )
                {
                    final EmbeddableAttributes e =
                        JAXB.unmarshal( new DOMSource( c.element ), EmbeddableAttributes.class );

                    embeddable.setAttributes( e );
                    acknowledge = true;
                }

                if ( acknowledge && !c.isAcknowledged() )
                {
                    c.markAsAcknowledged();
                }
            }
        }
    }

    private void customizeMappedSuperclass( final CCustomizations customizations, final MappedSuperclass ms )
    {
        for ( CPluginCustomization c : customizations )
        {
            if ( c.element != null && c.element.getNamespaceURI().equals( ORM_NS ) )
            {
                boolean acknowledge = false;

                if ( c.element.getLocalName().equals( "description" ) )
                {
                    ms.setDescription( c.element.getFirstChild().getNodeValue() );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "id-class" ) )
                {
                    final IdClass e = JAXB.unmarshal( new DOMSource( c.element ), IdClass.class );
                    ms.setIdClass( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "exclude-default-listeners" ) )
                {
                    final EmptyType e = JAXB.unmarshal( new DOMSource( c.element ), EmptyType.class );
                    ms.setExcludeDefaultListeners( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "exclude-superclass-listeners" ) )
                {
                    final EmptyType e = JAXB.unmarshal( new DOMSource( c.element ), EmptyType.class );
                    ms.setExcludeSuperclassListeners( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "entity-listeners" ) )
                {
                    final EntityListeners e = JAXB.unmarshal( new DOMSource( c.element ), EntityListeners.class );
                    ms.setEntityListeners( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "pre-persist" ) )
                {
                    final PrePersist e = JAXB.unmarshal( new DOMSource( c.element ), PrePersist.class );
                    ms.setPrePersist( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "post-persist" ) )
                {
                    final PostPersist e = JAXB.unmarshal( new DOMSource( c.element ), PostPersist.class );
                    ms.setPostPersist( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "pre-remove" ) )
                {
                    final PreRemove e = JAXB.unmarshal( new DOMSource( c.element ), PreRemove.class );
                    ms.setPreRemove( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "post-remove" ) )
                {
                    final PostRemove e = JAXB.unmarshal( new DOMSource( c.element ), PostRemove.class );
                    ms.setPostRemove( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "pre-update" ) )
                {
                    final PreUpdate e = JAXB.unmarshal( new DOMSource( c.element ), PreUpdate.class );
                    ms.setPreUpdate( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "post-update" ) )
                {
                    final PostUpdate e = JAXB.unmarshal( new DOMSource( c.element ), PostUpdate.class );
                    ms.setPostUpdate( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "post-load" ) )
                {
                    final PostLoad e = JAXB.unmarshal( new DOMSource( c.element ), PostLoad.class );
                    ms.setPostLoad( e );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "attributes" ) )
                {
                    final Attributes e = JAXB.unmarshal( new DOMSource( c.element ), Attributes.class );
                    ms.setAttributes( e );
                    acknowledge = true;
                }

                if ( acknowledge && !c.isAcknowledged() )
                {
                    c.markAsAcknowledged();
                }
            }
        }
    }

    private void customizePersistenceUnit(
        final CCustomizations customizations, final Persistence.PersistenceUnit unit )
    {
        for ( CPluginCustomization c : customizations )
        {
            if ( c.element != null && c.element.getNamespaceURI().equals( PERSISTENCE_NS ) )
            {
                boolean acknowledge = false;

                if ( c.element.getLocalName().equals( "description" ) )
                {
                    unit.setDescription( c.element.getFirstChild().getNodeValue() );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "provider" ) )
                {
                    unit.setProvider( c.element.getFirstChild().getNodeValue() );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "jta-data-source" ) )
                {
                    unit.setJtaDataSource( c.element.getFirstChild().getNodeValue() );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "non-jta-data-source" ) )
                {
                    unit.setNonJtaDataSource( c.element.getFirstChild().getNodeValue() );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "mapping-file" ) )
                {
                    unit.getMappingFile().add( c.element.getFirstChild().getNodeValue() );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "jar-file" ) )
                {
                    unit.getJarFile().add( c.element.getFirstChild().getNodeValue() );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "class" ) )
                {
                    unit.getClazz().add( c.element.getFirstChild().getNodeValue() );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "exclude-unlisted-classes" ) )
                {
                    unit.setExcludeUnlistedClasses( Boolean.valueOf( c.element.getFirstChild().getNodeValue() ) );
                    acknowledge = true;
                }
                else if ( c.element.getLocalName().equals( "properties" ) )
                {
                    final Persistence.PersistenceUnit.Properties e =
                        JAXB.unmarshal( new DOMSource( c.element ), Persistence.PersistenceUnit.Properties.class );

                    unit.setProperties( e );
                    acknowledge = true;
                }

                if ( acknowledge && !c.isAcknowledged() )
                {
                    c.markAsAcknowledged();
                }
            }
        }
    }

    // --
    // Code generation methods.
    // --
    private void generateProperty( final String name, final Class<?> type, final ClassOutline c )
    {
        final char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase( chars[0] );
        final String publicName = String.valueOf( chars );
        final String getterName = ( type == Boolean.TYPE || type == Boolean.class ? "is" : "get" ) + publicName;
        final JFieldVar field = c.implClass.field( JMod.PROTECTED, type, name );
        field.annotate( XmlTransient.class );

        final JMethod getter = c.implClass.method( JMod.PUBLIC, type, getterName );
        getter.body().directStatement( "// " + getMessage( "title" ) );
        getter.body()._return( field );

        final JMethod setter = c.implClass.method( JMod.PUBLIC, c.parent().getCodeModel().VOID,
                                                   "set" + String.valueOf( chars ) );

        final JVar valueParam = setter.param( JMod.FINAL, type, "value" );
        setter.body().directStatement( "// " + getMessage( "title" ) );
        setter.body().assign( JExpr._this().ref( field ), valueParam );

        getter.javadoc().append( "Gets the value of the " + name + " property." );
        getter.javadoc().addReturn().append( "The value of the " + name + " property." );

        setter.javadoc().append( "Sets the value of the " + name + " property." );
        setter.javadoc().addParam( valueParam ).append( "The new value of the " + name + " property." );
    }

    private void generateCollectionSetter( final JCodeModel cm, final ClassOutline c, final CPropertyInfo p )
    {
        final JFieldVar field = c.implClass.fields().get( p.getName( false ) );
        final JMethod setter = c.implClass.method( JMod.PUBLIC, cm.VOID, "set" + p.getName( true ) );
        final JVar valueParam = setter.param( JMod.FINAL, field.type(), "value" );
        final JBlock body = setter.body();
        body.directStatement( "// " + getMessage( "title" ) );
        body.assign( JExpr._this().ref( field ), valueParam );

        setter.javadoc().append( "Sets the value of the " + p.getName( false ) + " property." );
        setter.javadoc().addParam( valueParam ).append( "The new value of the " + p.getName( false ) + " property." );
    }

    private void generateTemporalBasic( final FieldOutline f )
    {
        // Getter.
        final String lineSeparator = System.getProperty( "line.separator" );
        final JFieldVar field = f.parent().implClass.field( JMod.PROTECTED, f.parent().parent().getCodeModel().ref(
            "java.util.Calendar" ), "jpa" + f.getPropertyInfo().getName( true ) );

        field.annotate( XmlTransient.class );

        final JMethod getter =
            f.parent().implClass.method( JMod.PUBLIC, f.parent().parent().getCodeModel().ref( Calendar.class ),
                                         "getJpa" + f.getPropertyInfo().getName( true ) );

        getter.body().directStatement( "// " + getMessage( "title" ) );
        getter.body().assign( JExpr.refthis( field.name() ), f.parent()._package().objectFactory().
            staticInvoke( "createCalendar" ).arg( JExpr.refthis( f.getPropertyInfo().getName( false ) ) ) );

        getter.body()._return( JExpr.refthis( field.name() ) );

        getter.javadoc().append(
            "Gets the value of the jpa" + f.getPropertyInfo().getName( true ) + " property." + lineSeparator );

        getter.javadoc().append(
            "<p>This method returns the value of the " + f.getPropertyInfo().getName( false ) + " property "
            + "transformed to a " + Calendar.class.getName() + " instance.</p>" + lineSeparator );

        getter.javadoc().addReturn().append(
            "The value of the jpa" + f.getPropertyInfo().getName( true ) + " property." );

        // Setter.
        final JMethod setter = f.parent().implClass.method(
            JMod.PUBLIC, f.parent().parent().getCodeModel().VOID, "setJpa" + f.getPropertyInfo().getName( true ) );

        final JVar calendar = setter.param( JMod.FINAL, Calendar.class, "value" );

        setter.body().directStatement( "// " + getMessage( "title" ) );
        setter.body().assign( JExpr.refthis( field.name() ), calendar );
        setter.body().assign( JExpr.refthis( f.getPropertyInfo().getName( false ) ), f.parent()._package().
            objectFactory().staticInvoke( "createXMLGregorianCalendar" ).arg( calendar ) );

        // Update to the JAXB property setter to also update the jpa field.
        final JMethod transientSetter = this.getSetter( f );
        transientSetter.body().assign( JExpr.refthis( field.name() ), f.parent()._package().objectFactory().
            staticInvoke( "createCalendar" ).arg( transientSetter.listParams()[0] ) );

        setter.javadoc().append(
            "Sets the value of the jpa" + f.getPropertyInfo().getName( true ) + " property." + lineSeparator );

        setter.javadoc().append(
            "<p>This method sets the value of the " + f.getPropertyInfo().getName( false ) + " property by "
            + "transforming {@code " + calendar.name() + "} to a " + XMLGregorianCalendar.class.getName()
            + " instance.</p>" + lineSeparator );

        setter.javadoc().addParam( calendar ).append(
            "The new value of the jpa" + f.getPropertyInfo().getName( true ) + " property." );

    }

    private void generateAdapterMethods( final JCodeModel cm, final PackageOutline p )
    {
        final JDefinedClass of = p.objectFactory();

        // createCalendar
        final JMethod createCalendar = of.method( JMod.STATIC, cm.ref( Calendar.class ), "createCalendar" );
        JVar value = createCalendar.param( JMod.FINAL, XMLGregorianCalendar.class, "value" );
        createCalendar.body().directStatement( "// " + getMessage( "title" ) );
        createCalendar.body()._return(
            JOp.cond( value.eq( JExpr._null() ), JExpr._null(), value.invoke( "toGregorianCalendar" ) ) );

        createCalendar.javadoc().append( "Creates a " + Calendar.class.getName() + " instance from a "
                                         + XMLGregorianCalendar.class.getName() + " instance." );

        createCalendar.javadoc().addParam( value ).append(
            "The " + XMLGregorianCalendar.class.getName() + " instance or {@code null}." );

        createCalendar.javadoc().addReturn().append(
            "A " + Calendar.class.getName() + " instance created from {@code " + value.name()
            + "} or {@code null} if {@code " + value.name() + "} is {@code null}." );

        // createXMLGregorianCalendar
        final JMethod createXMLGregorianCalendar =
            of.method( JMod.STATIC, XMLGregorianCalendar.class, "createXMLGregorianCalendar" );

        createXMLGregorianCalendar.body().directStatement( "// " + getMessage( "title" ) );
        value = createXMLGregorianCalendar.param( JMod.FINAL, Calendar.class, "value" );

        final JTryBlock tryBlock = createXMLGregorianCalendar.body()._try();
        final JConditional notNull = tryBlock.body()._if( value.ne( JExpr._null() ) );

        final JVar calendar = notNull._then().decl( cm.ref( GregorianCalendar.class ), "calendar" );
        calendar.init( JExpr._new( cm.ref( GregorianCalendar.class ) ) );
        notNull._then().add( calendar.invoke( "setTimeZone" ).arg( value.invoke( "getTimeZone" ) ) );
        notNull._then().add( calendar.invoke( "setTimeInMillis" ).arg( value.invoke( "getTimeInMillis" ) ) );
        notNull._then()._return( cm.ref( DatatypeFactory.class ).staticInvoke( "newInstance" ).invoke(
            "newXMLGregorianCalendar" ).arg( calendar ) );

        tryBlock.body()._return( JExpr._null() );

        final JCatchBlock catchBlock = tryBlock._catch( cm.ref( DatatypeConfigurationException.class ) );
        catchBlock.body()._throw( JExpr._new( cm.ref( AssertionError.class ) ).arg( catchBlock.param( "e" ) ) );

        createXMLGregorianCalendar.javadoc().append(
            "Creates a " + XMLGregorianCalendar.class.getName() + " instance from a " + Calendar.class.getName()
            + " instance." );

        createXMLGregorianCalendar.javadoc().addParam( value ).append(
            "The " + Calendar.class.getName() + " instance or {@code null}." );

        createXMLGregorianCalendar.javadoc().addReturn().append(
            "A " + XMLGregorianCalendar.class.getName() + " instance created from {@code " + value.name()
            + "} or {@code null} if {@code " + value.name() + "} is {@code null}." );

    }

    // --
    // Methods for annotating classes.
    // --
    private void annotate( final Outline outline, final EntityMappings orm )
    {
        for ( Iterator<Entity> it = orm.getEntity().iterator(); it.hasNext(); )
        {
            this.annotateEntity( outline, it.next() );
        }
        for ( Iterator<Embeddable> it = orm.getEmbeddable().iterator(); it.hasNext(); )
        {
            this.annotateEmbeddable( outline, it.next() );
        }
        for ( Iterator<MappedSuperclass> it = orm.getMappedSuperclass().iterator(); it.hasNext(); )
        {
            this.annotateMappedSuperclass( outline, it.next() );
        }
    }

    private void annotateMappedSuperclass( final Outline outline, final MappedSuperclass ms )
    {
        final JCodeModel cm = outline.getCodeModel();
        final ClassOutline c = this.getClassOutline( outline, ms.getClazz() );
        c.implClass.annotate( cm.ref( javax.persistence.MappedSuperclass.class ) );

        if ( ms.getAttributes() != null )
        {
            this.annotate( cm, c, ms.getAttributes() );
        }
        if ( ms.getEntityListeners() != null )
        {
            this.annotate( c, ms.getEntityListeners() );
        }
        if ( ms.getExcludeDefaultListeners() != null )
        {
            c.implClass.annotate( c.parent().getCodeModel().ref( javax.persistence.ExcludeDefaultListeners.class ) );
        }
        if ( ms.getExcludeSuperclassListeners() != null )
        {
            c.implClass.annotate( c.parent().getCodeModel().ref( javax.persistence.ExcludeSuperclassListeners.class ) );
        }
        if ( ms.getIdClass() != null )
        {
            this.annotate( c, ms.getIdClass() );
        }
        if ( ms.getPostLoad() != null )
        {
            final JMethod m = this.getMethod( c, ms.getPostLoad().getMethodName() );
            m.annotate( cm.ref( javax.persistence.PostLoad.class ) );
        }
        if ( ms.getPostPersist() != null )
        {
            final JMethod m = this.getMethod( c, ms.getPostPersist().getMethodName() );
            m.annotate( cm.ref( javax.persistence.PostPersist.class ) );
        }
        if ( ms.getPostRemove() != null )
        {
            final JMethod m = this.getMethod( c, ms.getPostRemove().getMethodName() );
            m.annotate( cm.ref( javax.persistence.PostRemove.class ) );
        }
        if ( ms.getPostUpdate() != null )
        {
            final JMethod m = this.getMethod( c, ms.getPostUpdate().getMethodName() );
            m.annotate( cm.ref( javax.persistence.PostUpdate.class ) );
        }
        if ( ms.getPrePersist() != null )
        {
            final JMethod m = this.getMethod( c, ms.getPreUpdate().getMethodName() );
            m.annotate( cm.ref( javax.persistence.PrePersist.class ) );
        }
        if ( ms.getPreRemove() != null )
        {
            final JMethod m = this.getMethod( c, ms.getPreRemove().getMethodName() );
            m.annotate( cm.ref( javax.persistence.PreRemove.class ) );
        }
        if ( ms.getPreUpdate() != null )
        {
            final JMethod m = this.getMethod( c, ms.getPreUpdate().getMethodName() );
            m.annotate( cm.ref( javax.persistence.PreUpdate.class ) );
        }
    }

    private void annotateEmbeddable( final Outline outline, final Embeddable embeddable )
    {
        final JCodeModel cm = outline.getCodeModel();
        final ClassOutline c = this.getClassOutline( outline, embeddable.getClazz() );
        c.implClass.annotate( cm.ref( javax.persistence.Embeddable.class ) );

        if ( embeddable.getAttributes() != null )
        {
            this.annotate( c, embeddable.getAttributes() );
        }
    }

    private void annotateEntity( final Outline outline, final Entity entity )
    {
        final JCodeModel cm = outline.getCodeModel();
        final ClassOutline c = this.getClassOutline( outline, entity.getClazz() );
        final JAnnotationUse a = c.implClass.annotate( cm.ref( javax.persistence.Entity.class ) );

        if ( entity.getName() != null )
        {
            a.param( "name", entity.getName() );
        }

        if ( !entity.getAssociationOverride().isEmpty() )
        {
            final JAnnotationUse aolst = c.implClass.annotate( cm.ref( javax.persistence.AssociationOverrides.class ) );
            final JAnnotationArrayMember value = aolst.paramArray( "value" );
            for ( AssociationOverride o : entity.getAssociationOverride() )
            {
                final JAnnotationUse ao = value.annotate( cm.ref( javax.persistence.AssociationOverride.class ) );
                if ( o.getName() != null )
                {
                    ao.param( "name", o.getName() );
                }

                if ( !o.getJoinColumn().isEmpty() )
                {
                    final JAnnotationArrayMember joinColumns = ao.paramArray( "joinColumns" );
                    for ( JoinColumn jc : o.getJoinColumn() )
                    {
                        final JAnnotationUse jca = joinColumns.annotate( cm.ref( javax.persistence.JoinColumn.class ) );
                        this.annotate( jca, jc );
                    }
                }
            }
        }

        if ( !entity.getAttributeOverride().isEmpty() )
        {
            final JAnnotationUse aolst = c.implClass.annotate( cm.ref( javax.persistence.AttributeOverrides.class ) );
            final JAnnotationArrayMember value = aolst.paramArray( "value" );
            for ( AttributeOverride o : entity.getAttributeOverride() )
            {
                final JAnnotationUse ao = value.annotate( cm.ref( javax.persistence.AttributeOverride.class ) );
                if ( o.getColumn() != null )
                {
                    final JAnnotationUse ac = ao.param( "column", cm.ref( javax.persistence.Column.class ) );
                    this.annotate( ac, o.getColumn() );
                }
                if ( o.getName() != null )
                {
                    ao.param( "name", o.getName() );
                }
            }
        }

        if ( entity.getAttributes() != null )
        {
            this.annotate( cm, c, entity.getAttributes() );
        }

        if ( entity.getDiscriminatorColumn() != null )
        {
            final JAnnotationUse dc = c.implClass.annotate( cm.ref( javax.persistence.DiscriminatorColumn.class ) );
            if ( entity.getDiscriminatorColumn().getColumnDefinition() != null )
            {
                dc.param( "columnDefinition", entity.getDiscriminatorColumn().getColumnDefinition() );
            }
            if ( entity.getDiscriminatorColumn().getDiscriminatorType() != null )
            {
                dc.param( "discriminatorType", javax.persistence.DiscriminatorType.valueOf(
                    entity.getDiscriminatorColumn().getDiscriminatorType().value() ) );

            }
            if ( entity.getDiscriminatorColumn().getLength() != null )
            {
                dc.param( "length", entity.getDiscriminatorColumn().getLength().intValue() );
            }
            if ( entity.getDiscriminatorColumn().getName() != null )
            {
                dc.param( "name", entity.getDiscriminatorColumn().getName() );
            }
        }

        if ( entity.getDiscriminatorValue() != null )
        {
            final JAnnotationUse dv = c.implClass.annotate( cm.ref( javax.persistence.DiscriminatorValue.class ) );
            dv.param( "value", entity.getDiscriminatorValue() );
        }

        if ( entity.getEntityListeners() != null )
        {
            this.annotate( c, entity.getEntityListeners() );
        }

        if ( entity.getExcludeDefaultListeners() != null )
        {
            c.implClass.annotate( cm.ref( javax.persistence.ExcludeDefaultListeners.class ) );
        }
        if ( entity.getExcludeSuperclassListeners() != null )
        {
            c.implClass.annotate( cm.ref( javax.persistence.ExcludeSuperclassListeners.class ) );
        }
        if ( entity.getIdClass() != null )
        {
            this.annotate( c, entity.getIdClass() );
        }
        if ( entity.getInheritance() != null )
        {
            final JAnnotationUse ih = c.implClass.annotate( cm.ref( javax.persistence.Inheritance.class ) );
            ih.param( "strategy",
                      javax.persistence.InheritanceType.valueOf( entity.getInheritance().getStrategy().value() ) );

        }
        if ( !entity.getNamedNativeQuery().isEmpty() )
        {
            final JAnnotationUse nnqlst = c.implClass.annotate( cm.ref( javax.persistence.NamedNativeQueries.class ) );
            final JAnnotationArrayMember value = nnqlst.paramArray( "value" );
            for ( NamedNativeQuery q : entity.getNamedNativeQuery() )
            {
                final JAnnotationUse qa = value.annotate( cm.ref( javax.persistence.NamedNativeQuery.class ) );
                qa.param( "name", q.getName() );
                qa.param( "query", q.getQuery() );

                if ( q.getResultClass() != null )
                {
                    qa.param( "resultClass", cm.ref( q.getResultClass() ) );
                }
                if ( q.getResultSetMapping() != null )
                {
                    qa.param( "resultSetMapping", q.getResultSetMapping() );
                }
                if ( !q.getHint().isEmpty() )
                {
                    final JAnnotationArrayMember hints = qa.paramArray( "hints" );
                    for ( QueryHint hint : q.getHint() )
                    {
                        final JAnnotationUse qh = hints.annotate( javax.persistence.QueryHint.class );
                        qh.param( "name", hint.getName() );
                        qh.param( "value", hint.getValue() );
                    }
                }
            }
        }
        if ( !entity.getNamedQuery().isEmpty() )
        {
            final JAnnotationUse nqlst = c.implClass.annotate( cm.ref( javax.persistence.NamedQueries.class ) );
            final JAnnotationArrayMember value = nqlst.paramArray( "value" );
            for ( NamedQuery q : entity.getNamedQuery() )
            {
                final JAnnotationUse nq = value.annotate( cm.ref( javax.persistence.NamedQuery.class ) );
                nq.param( "name", q.getName() );
                nq.param( "query", q.getQuery() );

                if ( !q.getHint().isEmpty() )
                {
                    final JAnnotationArrayMember hints = nq.paramArray( "hints" );
                    for ( QueryHint hint : q.getHint() )
                    {
                        final JAnnotationUse qh = hints.annotate( javax.persistence.QueryHint.class );
                        qh.param( "name", hint.getName() );
                        qh.param( "value", hint.getValue() );
                    }
                }
            }
        }
        if ( entity.getPostLoad() != null )
        {
            final JMethod m = this.getMethod( c, entity.getPostLoad().getMethodName() );
            m.annotate( cm.ref( javax.persistence.PostLoad.class ) );
        }
        if ( entity.getPostPersist() != null )
        {
            final JMethod m = this.getMethod( c, entity.getPostPersist().getMethodName() );
            m.annotate( cm.ref( javax.persistence.PostPersist.class ) );
        }
        if ( entity.getPostRemove() != null )
        {
            final JMethod m = this.getMethod( c, entity.getPostRemove().getMethodName() );
            m.annotate( cm.ref( javax.persistence.PostRemove.class ) );
        }
        if ( entity.getPostUpdate() != null )
        {
            final JMethod m = this.getMethod( c, entity.getPostUpdate().getMethodName() );
            m.annotate( cm.ref( javax.persistence.PostUpdate.class ) );
        }
        if ( entity.getPrePersist() != null )
        {
            final JMethod m = this.getMethod( c, entity.getPreUpdate().getMethodName() );
            m.annotate( cm.ref( javax.persistence.PrePersist.class ) );
        }
        if ( entity.getPreRemove() != null )
        {
            final JMethod m = this.getMethod( c, entity.getPreRemove().getMethodName() );
            m.annotate( cm.ref( javax.persistence.PreRemove.class ) );
        }
        if ( entity.getPreUpdate() != null )
        {
            final JMethod m = this.getMethod( c, entity.getPreUpdate().getMethodName() );
            m.annotate( cm.ref( javax.persistence.PreUpdate.class ) );
        }
        if ( !entity.getPrimaryKeyJoinColumn().isEmpty() )
        {
            final JAnnotationUse pkjcs = c.implClass.annotate( cm.ref( javax.persistence.PrimaryKeyJoinColumns.class ) );
            final JAnnotationArrayMember pkjc = pkjcs.paramArray( "value" );
            this.annotate( cm, pkjc, entity.getPrimaryKeyJoinColumn() );
        }
        if ( !entity.getSecondaryTable().isEmpty() )
        {
            final JAnnotationUse stlst = c.implClass.annotate( cm.ref( javax.persistence.SecondaryTables.class ) );
            final JAnnotationArrayMember value = stlst.paramArray( "value" );
            for ( SecondaryTable t : entity.getSecondaryTable() )
            {
                final JAnnotationUse st = value.annotate( cm.ref( javax.persistence.SecondaryTable.class ) );
                if ( t.getCatalog() != null )
                {
                    st.param( "catalog", t.getCatalog() );
                }
                if ( t.getName() != null )
                {
                    st.param( "name", t.getName() );
                }
                if ( !t.getPrimaryKeyJoinColumn().isEmpty() )
                {
                    final JAnnotationArrayMember pkjc = st.paramArray( "pkJoinColumns" );
                    this.annotate( cm, pkjc, entity.getPrimaryKeyJoinColumn() );
                }
                if ( t.getSchema() != null )
                {
                    st.param( "schema", t.getSchema() );
                }
                if ( !t.getUniqueConstraint().isEmpty() )
                {
                    final JAnnotationArrayMember uca = st.paramArray( "uniqueConstraints" );
                    for ( UniqueConstraint uc : t.getUniqueConstraint() )
                    {
                        final JAnnotationUse u = uca.annotate( javax.persistence.UniqueConstraint.class );
                        final JAnnotationArrayMember colNames = u.paramArray( "columnNames" );
                        for ( String cn : uc.getColumnName() )
                        {
                            colNames.param( cn );
                        }
                    }
                }
            }
        }
        if ( entity.getSequenceGenerator() != null )
        {
            final JAnnotationUse sg = c.implClass.annotate( cm.ref( javax.persistence.SequenceGenerator.class ) );
            this.annotate( sg, entity.getSequenceGenerator() );
        }
        if ( !entity.getSqlResultSetMapping().isEmpty() )
        {
            final JAnnotationUse lst = c.implClass.annotate( cm.ref( javax.persistence.SqlResultSetMappings.class ) );
            final JAnnotationArrayMember value = lst.paramArray( "value" );
            for ( SqlResultSetMapping m : entity.getSqlResultSetMapping() )
            {
                final JAnnotationUse srsm = value.annotate( cm.ref( javax.persistence.SqlResultSetMapping.class ) );
                if ( !m.getColumnResult().isEmpty() )
                {
                    final JAnnotationArrayMember cols = srsm.paramArray( "columns" );
                    for ( ColumnResult cr : m.getColumnResult() )
                    {
                        final JAnnotationUse cra = cols.annotate( javax.persistence.ColumnResult.class );
                        cra.param( "name", cr.getName() );
                    }
                }
                if ( !m.getEntityResult().isEmpty() )
                {
                    final JAnnotationArrayMember entities = srsm.paramArray( "entities" );
                    for ( EntityResult er : m.getEntityResult() )
                    {
                        final JAnnotationUse era = entities.annotate( javax.persistence.EntityResult.class );
                        if ( er.getDiscriminatorColumn() != null )
                        {
                            era.param( "discriminatorColumn", er.getDiscriminatorColumn() );
                        }
                        if ( er.getEntityClass() != null )
                        {
                            era.param( "entityClass", cm.ref( er.getEntityClass() ) );
                        }
                        if ( !er.getFieldResult().isEmpty() )
                        {
                            final JAnnotationArrayMember fields = era.paramArray( "fields" );
                            for ( FieldResult fr : er.getFieldResult() )
                            {
                                final JAnnotationUse fra = fields.annotate( javax.persistence.FieldResult.class );
                                if ( fr.getColumn() != null )
                                {
                                    fra.param( "column", fr.getColumn() );
                                }
                                if ( fr.getName() != null )
                                {
                                    fra.param( "name", fr.getName() );
                                }
                            }
                        }
                    }
                }
                if ( m.getName() != null )
                {
                    srsm.param( "name", m.getName() );
                }
            }
        }
        if ( entity.getTable() != null )
        {
            final JAnnotationUse ta = c.implClass.annotate( cm.ref( javax.persistence.Table.class ) );
            if ( entity.getTable().getCatalog() != null )
            {
                ta.param( "catalog", entity.getTable().getCatalog() );
            }
            if ( entity.getTable().getName() != null )
            {
                ta.param( "name", entity.getTable().getName() );
            }
            if ( entity.getTable().getSchema() != null )
            {
                ta.param( "schema", entity.getTable().getSchema() );
            }
            if ( !entity.getTable().getUniqueConstraint().isEmpty() )
            {
                final JAnnotationArrayMember uclst = ta.paramArray( "uniqueConstraints" );
                for ( UniqueConstraint uc : entity.getTable().getUniqueConstraint() )
                {
                    final JAnnotationUse uca = uclst.annotate( javax.persistence.UniqueConstraint.class );
                    final JAnnotationArrayMember colNames = uca.paramArray( "columnNames" );
                    for ( String cn : uc.getColumnName() )
                    {
                        colNames.param( cn );
                    }
                }
            }
        }
        if ( entity.getTableGenerator() != null )
        {
            final JAnnotationUse tg = c.implClass.annotate( cm.ref( javax.persistence.TableGenerator.class ) );
            this.annotate( tg, entity.getTableGenerator() );
        }
    }

    private void annotate( final ClassOutline c, final IdClass id )
    {
        final JAnnotationUse a =
            c.implClass.annotate( c.parent().getCodeModel().ref( javax.persistence.IdClass.class ) );

        a.param( "value", c.parent().getCodeModel().ref( id.getClazz() ) );
    }

    private void annotate( final ClassOutline c, final EntityListeners l )
    {
        final JAnnotationUse lst =
            c.implClass.annotate( c.parent().getCodeModel().ref( javax.persistence.EntityListeners.class ) );

        final JAnnotationArrayMember value = lst.paramArray( "value" );
        for ( EntityListener el : l.getEntityListener() )
        {
            value.param( c.parent().getCodeModel().ref( el.getClazz() ) );
        }
    }

    private void annotate( final JAnnotationUse a, final Column column )
    {
        if ( column.isInsertable() != null )
        {
            a.param( "insertable", column.isInsertable().booleanValue() );
        }
        if ( column.isNullable() != null )
        {
            a.param( "nullable", column.isNullable().booleanValue() );
        }
        if ( column.isUnique() != null )
        {
            a.param( "unique", column.isUnique().booleanValue() );
        }
        if ( column.isUpdatable() != null )
        {
            a.param( "updatable", column.isUpdatable().booleanValue() );
        }
        if ( column.getColumnDefinition() != null )
        {
            a.param( "columnDefinition", column.getColumnDefinition() );
        }
        if ( column.getLength() != null )
        {
            a.param( "length", column.getLength().intValue() );
        }
        if ( column.getName() != null )
        {
            a.param( "name", column.getName() );
        }
        if ( column.getPrecision() != null )
        {
            a.param( "precision", column.getPrecision().intValue() );
        }
        if ( column.getScale() != null )
        {
            a.param( "scale", column.getScale().intValue() );
        }
        if ( column.getTable() != null )
        {
            a.param( "table", column.getTable() );
        }
    }

    private void annotate( final JAnnotationUse a, final JoinColumn column )
    {
        if ( column.isInsertable() != null )
        {
            a.param( "insertable", column.isInsertable().booleanValue() );
        }
        if ( column.isNullable() != null )
        {
            a.param( "nullable", column.isNullable().booleanValue() );
        }
        if ( column.isUnique() != null )
        {
            a.param( "unique", column.isUnique().booleanValue() );
        }
        if ( column.isUpdatable() != null )
        {
            a.param( "updatable", column.isUpdatable().booleanValue() );
        }
        if ( column.getColumnDefinition() != null )
        {
            a.param( "columnDefinition", column.getColumnDefinition() );
        }
        if ( column.getName() != null )
        {
            a.param( "name", column.getName() );
        }
        if ( column.getReferencedColumnName() != null )
        {
            a.param( "referencedColumnName", column.getReferencedColumnName() );
        }
        if ( column.getTable() != null )
        {
            a.param( "table", column.getTable() );
        }
    }

    private void annotate( final JCodeModel cm, final JAnnotationUse a, final JoinTable jt )
    {
        if ( jt.getCatalog() != null )
        {
            a.param( "catalog", jt.getCatalog() );
        }
        if ( jt.getName() != null )
        {
            a.param( "name", jt.getName() );
        }
        if ( jt.getSchema() != null )
        {
            a.param( "schema", jt.getSchema() );
        }
        if ( !jt.getInverseJoinColumn().isEmpty() )
        {
            final JAnnotationArrayMember ijclst = a.paramArray( "inverseJoinColumns" );
            for ( JoinColumn jc : jt.getInverseJoinColumn() )
            {
                final JAnnotationUse ijc = ijclst.annotate( cm.ref( javax.persistence.JoinColumn.class ) );
                this.annotate( ijc, jc );
            }
        }
        if ( !jt.getJoinColumn().isEmpty() )
        {
            final JAnnotationArrayMember jclst = a.paramArray( "joinColumns" );
            for ( JoinColumn jc : jt.getJoinColumn() )
            {
                final JAnnotationUse jca = jclst.annotate( cm.ref( javax.persistence.JoinColumn.class ) );
                this.annotate( jca, jc );
            }
        }
        if ( !jt.getUniqueConstraint().isEmpty() )
        {
            final JAnnotationArrayMember uclst = a.paramArray( "uniqueConstraints" );
            for ( UniqueConstraint uc : jt.getUniqueConstraint() )
            {
                final JAnnotationUse uca = uclst.annotate( javax.persistence.UniqueConstraint.class );
                final JAnnotationArrayMember colNames = uca.paramArray( "columnNames" );
                for ( String cn : uc.getColumnName() )
                {
                    colNames.param( cn );
                }
            }
        }
    }

    private void annotate( final JCodeModel cm, final JAnnotationArrayMember a, final List<PrimaryKeyJoinColumn> cols )
    {
        for ( PrimaryKeyJoinColumn jc : cols )
        {
            final JAnnotationUse jca = a.annotate( cm.ref( javax.persistence.PrimaryKeyJoinColumn.class ) );
            if ( jc.getColumnDefinition() != null )
            {
                jca.param( "columnDefinition", jc.getColumnDefinition() );
            }
            if ( jc.getName() != null )
            {
                jca.param( "name", jc.getName() );
            }
            if ( jc.getReferencedColumnName() != null )
            {
                jca.param( "referenceColumnName", jc.getReferencedColumnName() );
            }
        }
    }

    private void annotate( final JAnnotationUse a, final SequenceGenerator gen )
    {
        if ( gen.getAllocationSize() != null )
        {
            a.param( "allocationSize", gen.getAllocationSize().intValue() );
        }
        if ( gen.getInitialValue() != null )
        {
            a.param( "initialValue", gen.getInitialValue().intValue() );
        }
        if ( gen.getName() != null )
        {
            a.param( "name", gen.getName() );
        }
        if ( gen.getSequenceName() != null )
        {
            a.param( "sequenceName", gen.getSequenceName() );
        }
    }

    private void annotate( final JAnnotationUse a, final TableGenerator gen )
    {
        if ( gen.getAllocationSize() != null )
        {
            a.param( "allocationSize", gen.getAllocationSize().intValue() );
        }
        if ( gen.getCatalog() != null )
        {
            a.param( "catalog", gen.getCatalog() );
        }
        if ( gen.getInitialValue() != null )
        {
            a.param( "initialValue", gen.getInitialValue().intValue() );
        }
        if ( gen.getName() != null )
        {
            a.param( "name", gen.getName() );
        }
        if ( gen.getPkColumnName() != null )
        {
            a.param( "pkColumnName", gen.getPkColumnName() );
        }
        if ( gen.getPkColumnValue() != null )
        {
            a.param( "pkColumnValue", gen.getPkColumnValue() );
        }
        if ( gen.getSchema() != null )
        {
            a.param( "schema", gen.getSchema() );
        }
        if ( gen.getTable() != null )
        {
            a.param( "table", gen.getTable() );
        }
        if ( gen.getValueColumnName() != null )
        {
            a.param( "valueColumnName", gen.getValueColumnName() );
        }
        if ( !gen.getUniqueConstraint().isEmpty() )
        {
            final JAnnotationArrayMember uclst = a.paramArray( "uniqueConstraints" );
            for ( UniqueConstraint uc : gen.getUniqueConstraint() )
            {
                final JAnnotationUse uca = uclst.annotate( javax.persistence.UniqueConstraint.class );
                final JAnnotationArrayMember colNames = uca.paramArray( "columnNames" );
                for ( String cn : uc.getColumnName() )
                {
                    colNames.param( cn );
                }
            }
        }
    }

    private void annotate( final JAnnotationUse a, final CascadeType c )
    {
        if ( c.getCascadeAll() != null )
        {
            a.param( "cascade", javax.persistence.CascadeType.ALL );
        }
        if ( c.getCascadeMerge() != null )
        {
            a.param( "cascade", javax.persistence.CascadeType.MERGE );
        }
        if ( c.getCascadePersist() != null )
        {
            a.param( "cascade", javax.persistence.CascadeType.PERSIST );
        }
        if ( c.getCascadeRefresh() != null )
        {
            a.param( "cascade", javax.persistence.CascadeType.REFRESH );
        }
        if ( c.getCascadeRemove() != null )
        {
            a.param( "cascade", javax.persistence.CascadeType.REMOVE );
        }
    }

    private void annotate( final JCodeModel cm, final ClassOutline c, final Attributes a )
    {
        for ( Basic b : a.getBasic() )
        {
            this.annotate( c, b );
        }

        for ( Embedded e : a.getEmbedded() )
        {
            final JMethod getter = this.getGetter( c, e.getName() );
            getter.annotate( cm.ref( javax.persistence.Embedded.class ) );
            if ( !e.getAttributeOverride().isEmpty() )
            {
                final JAnnotationUse aolst = getter.annotate( cm.ref( javax.persistence.AttributeOverrides.class ) );
                final JAnnotationArrayMember value = aolst.paramArray( "value" );
                for ( AttributeOverride o : e.getAttributeOverride() )
                {
                    final JAnnotationUse ao = value.annotate( cm.ref( javax.persistence.AttributeOverride.class ) );
                    if ( o.getColumn() != null )
                    {
                        final JAnnotationUse ac = ao.param( "column", cm.ref( javax.persistence.Column.class ) );
                        this.annotate( ac, o.getColumn() );
                    }
                    if ( o.getName() != null )
                    {
                        ao.param( "name", o.getName() );
                    }
                }
            }
        }

        if ( a.getEmbeddedId() != null )
        {
            final JMethod getter = this.getGetter( c, a.getEmbeddedId().getName() );
            getter.annotate( cm.ref( javax.persistence.EmbeddedId.class ) );
            if ( !a.getEmbeddedId().getAttributeOverride().isEmpty() )
            {
                final JAnnotationUse aolst = getter.annotate( cm.ref( javax.persistence.AttributeOverrides.class ) );
                final JAnnotationArrayMember value = aolst.paramArray( "value" );
                for ( AttributeOverride o : a.getEmbeddedId().getAttributeOverride() )
                {
                    final JAnnotationUse ao = value.annotate( cm.ref( javax.persistence.AttributeOverride.class ) );
                    if ( o.getColumn() != null )
                    {
                        final JAnnotationUse ac = ao.param( "column", cm.ref( javax.persistence.Column.class ) );
                        this.annotate( ac, o.getColumn() );
                    }
                    if ( o.getName() != null )
                    {
                        ao.param( "name", o.getName() );
                    }
                }
            }
        }

        for ( Id i : a.getId() )
        {
            final JMethod getter = this.getGetter( c, i.getName() );
            getter.annotate( cm.ref( javax.persistence.Id.class ) );
            if ( i.getColumn() != null )
            {
                final JAnnotationUse column = getter.annotate( cm.ref( javax.persistence.Column.class ) );
                this.annotate( column, i.getColumn() );
            }
            if ( i.getGeneratedValue() != null )
            {
                final JAnnotationUse gv = getter.annotate( cm.ref( javax.persistence.GeneratedValue.class ) );
                if ( i.getGeneratedValue().getGenerator() != null )
                {
                    gv.param( "generator", i.getGeneratedValue().getGenerator() );
                }
                if ( i.getGeneratedValue().getStrategy() != null )
                {
                    gv.param( "strategy", javax.persistence.GenerationType.valueOf(
                        i.getGeneratedValue().getStrategy().value() ) );

                }
            }
            if ( i.getSequenceGenerator() != null )
            {
                final JAnnotationUse gen = getter.annotate( cm.ref( javax.persistence.SequenceGenerator.class ) );
                this.annotate( gen, i.getSequenceGenerator() );
            }
            if ( i.getTableGenerator() != null )
            {
                final JAnnotationUse gen = getter.annotate( cm.ref( javax.persistence.TableGenerator.class ) );
                this.annotate( gen, i.getTableGenerator() );
            }
            if ( i.getTemporal() != null )
            {
                final JAnnotationUse temp = getter.annotate( cm.ref( javax.persistence.Temporal.class ) );
                temp.param( "value", javax.persistence.TemporalType.valueOf( i.getTemporal().value() ) );
            }
        }

        for ( ManyToMany m : a.getManyToMany() )
        {
            final JMethod getter = this.getGetter( c, m.getName() );
            final JAnnotationUse m2m = getter.annotate( cm.ref( javax.persistence.ManyToMany.class ) );

            if ( m.getCascade() != null )
            {
                this.annotate( m2m, m.getCascade() );
            }
            if ( m.getFetch() != null )
            {
                m2m.param( "fetch", javax.persistence.FetchType.valueOf( m.getFetch().value() ) );
            }
            if ( m.getJoinTable() != null )
            {
                final JAnnotationUse jt = getter.annotate( cm.ref( javax.persistence.JoinTable.class ) );
                this.annotate( cm, jt, m.getJoinTable() );
            }
            if ( m.getMapKey() != null )
            {
                final JAnnotationUse mk = getter.annotate( cm.ref( javax.persistence.MapKey.class ) );
                mk.param( "name", m.getMapKey().getName() );
            }
            if ( m.getMappedBy() != null )
            {
                m2m.param( "mappedBy", m.getMappedBy() );
            }
            if ( m.getOrderBy() != null )
            {
                final JAnnotationUse ob = getter.annotate( cm.ref( javax.persistence.OrderBy.class ) );
                ob.param( "value", m.getOrderBy() );
            }
            if ( m.getTargetEntity() != null )
            {
                m2m.param( "targetEntity", cm.ref( m.getTargetEntity() ) );
            }
        }

        for ( ManyToOne m : a.getManyToOne() )
        {
            final JMethod getter = this.getGetter( c, m.getName() );
            final JAnnotationUse m2o = getter.annotate( cm.ref( javax.persistence.ManyToOne.class ) );
            if ( m.getCascade() != null )
            {
                this.annotate( m2o, m.getCascade() );
            }
            if ( m.getFetch() != null )
            {
                m2o.param( "fetch", javax.persistence.FetchType.valueOf( m.getFetch().value() ) );
            }
            if ( !m.getJoinColumn().isEmpty() )
            {
                for ( JoinColumn jc : m.getJoinColumn() )
                {
                    final JAnnotationUse jca = getter.annotate( cm.ref( javax.persistence.JoinColumn.class ) );
                    this.annotate( jca, jc );
                }
            }
            if ( m.getJoinTable() != null )
            {
                final JAnnotationUse jt = getter.annotate( cm.ref( javax.persistence.JoinTable.class ) );
                this.annotate( cm, jt, m.getJoinTable() );
            }
            if ( m.getTargetEntity() != null )
            {
                m2o.param( "targetEntity", cm.ref( m.getTargetEntity() ) );
            }
            if ( m.isOptional() != null )
            {
                m2o.param( "optional", m.isOptional().booleanValue() );
            }
        }

        for ( OneToMany m : a.getOneToMany() )
        {
            final JMethod getter = this.getGetter( c, m.getName() );
            final JAnnotationUse o2m = getter.annotate( cm.ref( javax.persistence.OneToMany.class ) );

            if ( m.getCascade() != null )
            {
                this.annotate( o2m, m.getCascade() );
            }
            if ( m.getFetch() != null )
            {
                o2m.param( "fetch", javax.persistence.FetchType.valueOf( m.getFetch().value() ) );
            }
            if ( !m.getJoinColumn().isEmpty() )
            {
                for ( JoinColumn jc : m.getJoinColumn() )
                {
                    final JAnnotationUse jca = getter.annotate( cm.ref( javax.persistence.JoinColumn.class ) );
                    this.annotate( jca, jc );
                }
            }
            if ( m.getJoinTable() != null )
            {
                final JAnnotationUse jt = getter.annotate( cm.ref( javax.persistence.JoinTable.class ) );
                this.annotate( cm, jt, m.getJoinTable() );
            }
            if ( m.getMapKey() != null )
            {
                final JAnnotationUse mk = getter.annotate( cm.ref( javax.persistence.MapKey.class ) );
                mk.param( "name", m.getMapKey().getName() );
            }
            if ( m.getMappedBy() != null )
            {
                o2m.param( "mappedBy", m.getMappedBy() );
            }
            if ( m.getOrderBy() != null )
            {
                final JAnnotationUse ob = getter.annotate( cm.ref( javax.persistence.OrderBy.class ) );
                ob.param( "value", m.getOrderBy() );
            }
            if ( m.getTargetEntity() != null )
            {
                o2m.param( "targetEntity", cm.ref( m.getTargetEntity() ) );
            }
        }

        for ( OneToOne m : a.getOneToOne() )
        {
            final JMethod getter = this.getGetter( c, m.getName() );
            final JAnnotationUse o2o = getter.annotate( cm.ref( javax.persistence.OneToOne.class ) );
            if ( m.getCascade() != null )
            {
                this.annotate( o2o, m.getCascade() );
            }
            if ( m.isOptional() != null )
            {
                o2o.param( "optional", m.isOptional().booleanValue() );
            }
            if ( m.getFetch() != null )
            {
                o2o.param( "fetch", javax.persistence.FetchType.valueOf( m.getFetch().value() ) );
            }
            if ( !m.getJoinColumn().isEmpty() )
            {
                for ( JoinColumn jc : m.getJoinColumn() )
                {
                    final JAnnotationUse jca = getter.annotate( cm.ref( javax.persistence.JoinColumn.class ) );
                    this.annotate( jca, jc );
                }
            }
            if ( m.getJoinTable() != null )
            {
                final JAnnotationUse jt = getter.annotate( cm.ref( javax.persistence.JoinTable.class ) );
                this.annotate( cm, jt, m.getJoinTable() );
            }
            if ( m.getMappedBy() != null )
            {
                o2o.param( "mappedBy", m.getMappedBy() );
            }
            if ( !m.getPrimaryKeyJoinColumn().isEmpty() )
            {
                final JAnnotationUse pkjcs = getter.annotate( cm.ref( javax.persistence.PrimaryKeyJoinColumns.class ) );
                final JAnnotationArrayMember pkjc = pkjcs.paramArray( "value" );
                this.annotate( cm, pkjc, m.getPrimaryKeyJoinColumn() );
            }
            if ( m.getTargetEntity() != null )
            {
                o2o.param( "targetEntity", cm.ref( m.getTargetEntity() ) );
            }
        }

        for ( Transient t : a.getTransient() )
        {
            this.annotate( c, t );
        }

        for ( Version v : a.getVersion() )
        {
            final JMethod getter = this.getGetter( c, v.getName() );
            getter.annotate( cm.ref( javax.persistence.Version.class ) );
            if ( v.getColumn() != null )
            {
                final JAnnotationUse col = getter.annotate( cm.ref( javax.persistence.Column.class ) );
                this.annotate( col, v.getColumn() );
            }
            if ( v.getTemporal() != null )
            {
                final JAnnotationUse temp = getter.annotate( cm.ref( javax.persistence.Temporal.class ) );
                temp.param( "value", javax.persistence.TemporalType.valueOf( v.getTemporal().value() ) );
            }
        }
    }

    private void annotate( final ClassOutline c, final EmbeddableAttributes a )
    {
        if ( !a.getBasic().isEmpty() )
        {
            for ( Basic b : a.getBasic() )
            {
                this.annotate( c, b );
            }
        }
        if ( !a.getTransient().isEmpty() )
        {
            for ( Transient t : a.getTransient() )
            {
                this.annotate( c, t );
            }
        }
    }

    private void annotate( final ClassOutline c, final Transient t )
    {
        final JMethod getter = this.getGetter( c, t.getName() );
        getter.annotate( c.parent().getCodeModel().ref( javax.persistence.Transient.class ) );
    }

    private void annotate( final ClassOutline c, final Basic b )
    {
        final JMethod getter = this.getGetter( c, b.getName() );
        final JAnnotationUse ann =
            getter.annotate( c.parent().getCodeModel().ref( javax.persistence.Basic.class ) );

        if ( b.isOptional() != null )
        {
            ann.param( "optional", b.isOptional().booleanValue() );
        }
        if ( b.getFetch() != null )
        {
            ann.param( "fetch", javax.persistence.FetchType.valueOf( b.getFetch().value() ) );
        }
        if ( b.getColumn() != null )
        {
            final JAnnotationUse ac = getter.annotate(
                c.parent().getCodeModel().ref( javax.persistence.Column.class ) );
            this.annotate( ac, b.getColumn() );
        }
        if ( b.getEnumerated() != null )
        {
            final JAnnotationUse ac = getter.annotate(
                c.parent().getCodeModel().ref( javax.persistence.Enumerated.class ) );

            ac.param( "value", javax.persistence.EnumType.valueOf( b.getEnumerated().value() ) );
        }
        if ( b.getLob() != null )
        {
            getter.annotate( c.parent().getCodeModel().ref( javax.persistence.Lob.class ) );
        }
        if ( b.getTemporal() != null )
        {
            final JAnnotationUse ta =
                getter.annotate( c.parent().getCodeModel().ref( javax.persistence.Temporal.class ) );

            ta.param( "value", javax.persistence.TemporalType.valueOf( b.getTemporal().value() ) );
        }
    }

    // --
    // Queries
    // --
    private Attributes getAttributes( final EntityMappings orm, final String className )
    {
        for ( Entity e : orm.getEntity() )
        {
            if ( e.getClazz().equals( className ) )
            {
                if ( e.getAttributes() == null )
                {
                    e.setAttributes( new Attributes() );
                }

                return e.getAttributes();
            }
        }

        for ( MappedSuperclass e : orm.getMappedSuperclass() )
        {
            if ( e.getClazz().equals( className ) )
            {
                if ( e.getAttributes() == null )
                {
                    e.setAttributes( new Attributes() );
                }

                return e.getAttributes();
            }
        }

        return null;
    }

    private ClassOutline getClassOutline( final Outline outline, final String binaryName )
    {
        for ( ClassOutline c : outline.getClasses() )
        {
            if ( c.implClass.binaryName().equals( binaryName ) )
            {
                return c;
            }
        }

        return null;
    }

    private JMethod getMethod( final ClassOutline c, final String methodName )
    {
        for ( JMethod m : c.implClass.methods() )
        {
            if ( m.name().equals( methodName ) )
            {
                return m;
            }
        }

        return null;
    }

    private JType[] getBasicTypes()
    {
        final JCodeModel cm = new JCodeModel();
        return new JType[]
            {
                cm.BOOLEAN,
                cm.BOOLEAN.boxify(),
                cm.BOOLEAN.array(),
                cm.BOOLEAN.boxify().array(),
                cm.BYTE,
                cm.BYTE.boxify(),
                cm.BYTE.array(),
                cm.BYTE.boxify().array(),
                cm.CHAR,
                cm.CHAR.boxify(),
                cm.CHAR.array(),
                cm.CHAR.boxify().array(),
                cm.DOUBLE,
                cm.DOUBLE.boxify(),
                cm.DOUBLE.array(),
                cm.DOUBLE.boxify().array(),
                cm.FLOAT,
                cm.FLOAT.boxify(),
                cm.FLOAT.array(),
                cm.FLOAT.boxify().array(),
                cm.INT,
                cm.INT.boxify(),
                cm.INT.array(),
                cm.INT.boxify().array(),
                cm.LONG,
                cm.LONG.boxify(),
                cm.LONG.array(),
                cm.LONG.boxify().array(),
                cm.SHORT,
                cm.SHORT.boxify(),
                cm.SHORT.array(),
                cm.SHORT.boxify().array(),
                cm.ref( String.class ),
                cm.ref( BigInteger.class ),
                cm.ref( BigDecimal.class ),
                cm.ref( java.util.Date.class ),
                cm.ref( java.util.Calendar.class ),
                cm.ref( java.sql.Date.class ),
                cm.ref( java.sql.Time.class ),
                cm.ref( java.sql.Timestamp.class )
            };
    }

    private boolean isBasicFieldOutline( final FieldOutline f )
    {
        final JType getterType = this.getGetter( f ).type();

        for ( JType t : getBasicTypes() )
        {
            if ( getterType.binaryName().equals( t.binaryName() ) )
            {
                return true;
            }
        }

        final Collection<? extends CTypeInfo> types = f.getPropertyInfo().ref();
        if ( types.size() == 1 )
        {
            final CTypeInfo type = types.iterator().next();

            return type instanceof CEnumLeafInfo;
        }

        return false;
    }

    private JMethod getGetter( final ClassOutline c, final String fieldName )
    {
        JMethod getter = null;
        FieldOutline field = null;

        final char[] chars = c.parent().getModel().getNameConverter().toPropertyName( fieldName ).toCharArray();
        chars[0] = Character.toUpperCase( chars[0] );
        String publicName = String.valueOf( chars );

        for ( FieldOutline f : c.getDeclaredFields() )
        {
            if ( f.getPropertyInfo().getName( false ).equals( fieldName ) )
            {
                field = f;
                break;
            }
        }

        if ( field != null )
        {
            publicName = field.getPropertyInfo().getName( true );
        }

        getter = c.implClass.getMethod( "get" + publicName, NO_JTYPES );

        if ( getter == null )
        {
            getter = c.implClass.getMethod( "is" + publicName, NO_JTYPES );
        }

        return getter;
    }

    private JMethod getGetter( final FieldOutline f )
    {
        final JDefinedClass clazz = f.parent().implClass;
        final String name = f.getPropertyInfo().getName( true );
        JMethod getter = clazz.getMethod( "get" + name, NO_JTYPES );

        if ( getter == null )
        {
            getter = clazz.getMethod( "is" + name, NO_JTYPES );
        }

        return getter;
    }

    private JMethod getSetter( final FieldOutline f )
    {
        JMethod setter = null;
        final JMethod getter = this.getGetter( f );

        if ( getter != null )
        {
            final JType t = getter.type();
            final JDefinedClass clazz = f.parent().implClass;
            final String name = f.getPropertyInfo().getName( true );
            setter = clazz.getMethod( "set" + name, new JType[]
                {
                    t
                } );

        }

        return setter;
    }

    private XSSimpleType getSchemaSimpleType( final XSSimpleType type, final String name )
    {
        if ( type == null )
        {
            return null;
        }
        else if ( type.getOwnerSchema().getTargetNamespace().equals( XMLConstants.W3C_XML_SCHEMA_NS_URI )
                  && name.equals( type.getName() ) )
        {
            return type;
        }

        return this.getSchemaSimpleType( type.getSimpleBaseType(), name );
    }

    private static String getMessage( final String key, final Object... args )
    {
        return MessageFormat.format(
            ResourceBundle.getBundle( "net/sourceforge/jpaxjc/PluginImpl" ).getString( key ), args );

    }

    private void log( final Level level, final String key, final Object... args )
    {
        final StringBuffer b = new StringBuffer().append( "[" ).append( MESSAGE_PREFIX ).append( "] [" ).
            append( level.getLocalizedName() ).append( "] " ).append( getMessage( key, args ) );

        int logLevel = Level.WARNING.intValue();
        if ( this.options != null && !this.options.quiet )
        {
            if ( this.options.verbose )
            {
                logLevel = Level.INFO.intValue();
            }
            if ( this.options.debugMode )
            {
                logLevel = Level.ALL.intValue();
            }
        }

        if ( level.intValue() >= logLevel )
        {
            if ( level.intValue() <= Level.INFO.intValue() )
            {
                System.out.println( b.toString() );
            }
            else
            {
                System.err.println( b.toString() );
            }
        }
    }

}
