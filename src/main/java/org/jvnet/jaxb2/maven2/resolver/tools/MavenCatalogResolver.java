package org.jvnet.jaxb2.maven2.resolver.tools;

import java.net.URI;
import java.net.URL;

import org.apache.maven.plugin.logging.Log;
import org.codehaus.plexus.component.annotations.Component;
import org.jvnet.jaxb2.maven2.DependencyResourceResolver;
import org.jvnet.jaxb2.maven2.plugin.logging.NullLog;

import com.sun.org.apache.xml.internal.resolver.Catalog;
import com.sun.org.apache.xml.internal.resolver.CatalogManager;

@Component( role = MavenCatalogResolver.class, hint = "maven-jaxb2-plugin-catalogresolver" )
public class MavenCatalogResolver
    extends com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver
{
    private final HsMavenCatalogResolver hsMavenCatalogResolver;

    public MavenCatalogResolver( CatalogManager catalogManager, DependencyResourceResolver dependencyResourceResolver )
    {
        this( catalogManager, dependencyResourceResolver, NullLog.INSTANCE );
    }

    public MavenCatalogResolver(
                CatalogManager catalogManager,
                DependencyResourceResolver dependencyResourceResolver,
                Log log )
    {
        hsMavenCatalogResolver = new HsMavenCatalogResolver( catalogManager, dependencyResourceResolver, log );
    }

    @Override
    public Catalog getCatalog()
    {
        return hsMavenCatalogResolver.getCatalog();
    }

    @Override
    public String getResolvedEntity( String publicId, String systemId )
    {
         String resolved = hsMavenCatalogResolver.getResolvedEntity( publicId, systemId );

         if ( resolved != null )
        {
            URI uri = URI.create( resolved );

            if ( "classpath".equals( uri.getScheme() ) )
            {
                URL resource =
                    Thread.currentThread().getContextClassLoader().getResource( uri.getSchemeSpecificPart() );

                if ( resource == null )
                {
                    return null;
                }
                else
                {
                    resolved = resource.toString();
                }

            }
        }

        return resolved;
    }
}
