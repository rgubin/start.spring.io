package io.spring.start.site.extension.code.java.realpage;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ResourcesScanner {

	private static void collectURL(ResourceURLFilter f, Set<URL> s, URL u) {
		if (f == null || f.accept(u)) {
			s.add(u);
		}
	}

	private static void iterateFileSystem(File r, ResourceURLFilter f, Set<URL> s) throws IOException {
		File[] files = r.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				iterateFileSystem(file, f, s);
			}
			else if (file.isFile()) {
				collectURL(f, s, file.toURI().toURL());
			}
		}
	}

	private static void iterateJarFile(File file, ResourceURLFilter f, Set<URL> s) throws IOException {
		JarFile jFile = new JarFile(file);
		for (Enumeration<JarEntry> je = jFile.entries(); je.hasMoreElements();) {
			JarEntry j = je.nextElement();
			if (!j.isDirectory()) {
				collectURL(f, s, new URL("jar", "", file.toURI() + "!/" + j.getName()));
			}
		}
	}

	private static void iterateEntry(File p, ResourceURLFilter f, Set<URL> s) throws IOException {
		if (p.isDirectory()) {
			iterateFileSystem(p, f, s);
		}
		else if (p.isFile() && p.getName().toLowerCase().endsWith(".jar")) {
			iterateJarFile(p, f, s);
		}
	}

	public static Set<URL> getResourceURLs() throws IOException, URISyntaxException {
		return getResourceURLs(null);
	}

	public static Set<URL> getResourceURLs(ResourceURLFilter filter) throws IOException, URISyntaxException {
		Set<URL> collectedURLs = new HashSet<>();
		URLClassLoader ucl = (URLClassLoader) ClassLoader.getSystemClassLoader();
		for (URL url : ucl.getURLs()) {
			iterateEntry(new File(url.toURI()), filter, collectedURLs);
		}
		return collectedURLs;
	}

}