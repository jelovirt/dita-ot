/*
 * This file is part of the DITA Open Toolkit project.
 *
 * Copyright 2019 Jarno Elovirta
 *
 * See the accompanying LICENSE file for applicable license.
 */

package org.dita.dost.util;

import java.io.InputStream;
import java.net.URI;
import java.util.Objects;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.stream.StreamSource;

/**
 * URI resolver that folds over multiple resolvers and returns the final result.
 *
 * @deprecated since 4.1
 */
@Deprecated
public class DelegatingURIResolver implements URIResolver {

  private final URIResolver[] resolvers;

  public DelegatingURIResolver(URIResolver... resolvers) {
    this.resolvers = resolvers;
  }

  @Override
  public Source resolve(String href, String base) throws TransformerException {
    //        System.out.println(" DelegatingURIResolver resolve: " + href);
    final URI abs = getAbsolute(href, base);
    if (abs.getScheme() != null) {
      switch (abs.getScheme()) {
        case "plugin":
          {
            final String resource = abs
              .getSchemeSpecificPart()
              .replace(":", "/");
            final InputStream in = getClass()
              .getClassLoader()
              .getResourceAsStream(resource);
            if (in != null) {
              return new StreamSource(in, abs.toString());
            }
          }
        case "platform":
          {
            final String resource = abs
              .getSchemeSpecificPart()
              .replace(":", "/");
            final InputStream in = getClass()
              .getClassLoader()
              .getResourceAsStream(resource);
            if (in != null) {
              return new StreamSource(in, abs.toString());
            }
          }
      }
    }
    Source src = null;
    for (final URIResolver resolver : resolvers) {
      // XXX: This will create a redundant XMLReader for each call to resolve
      final Source res = resolver.resolve(
        src != null ? src.getSystemId() : href,
        base
      );
      if (res != null) {
        src = res;
      }
    }
    return src;
  }

  private URI getAbsolute(String href, String base) {
    final URI h = URI.create(href);
    if (base == null || h.isAbsolute()) {
      return h;
    }
    final URI b = URI.create(base);
    if (Objects.equals(b.getScheme(), "plugin")) {
      final String schemeSpecificPart = b.getSchemeSpecificPart();
      final int i = schemeSpecificPart.indexOf(":");
      final String plugin = schemeSpecificPart.substring(0, i);
      final String path = schemeSpecificPart.substring(i + 1);
      final URI rel = URI.create(path).resolve(h);
      return URI.create("plugin:" + plugin + ":" + rel);
    }
    return b.resolve(h);
  }
}
