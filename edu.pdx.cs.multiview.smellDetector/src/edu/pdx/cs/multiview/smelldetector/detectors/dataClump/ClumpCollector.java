package edu.pdx.cs.multiview.smelldetector.detectors.dataClump;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

import edu.pdx.cs.multiview.smelldetector.indexer.EhcacheFactory;

public class ClumpCollector {

	private static Map<String, ClumpCollector> clumpCollectorsAtProjectLevel = new HashMap<String, ClumpCollector>();

	private Cache dataClumpsCache;
	private IJavaProject project;
	
	public static ClumpCollector getClumpCollector(IJavaProject project) {
		ClumpCollector clumpCollector = clumpCollectorsAtProjectLevel.get(project.getElementName());
		if(clumpCollector == null){
			clumpCollector = createCumpCollector(project);
		}
		return clumpCollector;
	}

	private ClumpCollector(IJavaProject project) {
		String projectName = project.getElementName();
		String cacheName = projectName + "_dataclumps";
		dataClumpsCache = getEhcacheFactory().createCache(cacheName);
	}

	private static ClumpCollector createCumpCollector(IJavaProject project) {
		ClumpCollector clumpCollector = new ClumpCollector(project);
		clumpCollectorsAtProjectLevel.put(project.getElementName(), clumpCollector);
		return clumpCollector;
	}

	

	public void addToCache(ClumpSignature sig, IMethod m) {
		Element element = dataClumpsCache.get(sig);
		if (element == null || element.getObjectValue() == null) {
			ClumpGroup g = new ClumpGroup(sig, m);
			ClumpGroupHolder clumpGroupHolder = new ClumpGroupHolder(g);
			System.out.println(" Saving Group \n " + clumpGroupHolder);
			dataClumpsCache.put(new Element(sig, clumpGroupHolder));
		} else {
			ClumpGroupHolder existingGroup = (ClumpGroupHolder) element.getObjectValue();
			existingGroup.add(m);
		}
	}

	public List<ClumpGroup> inGroupOf(IMethod method) {
		try {
			List<ClumpSignature> sigs = ClumpSignature.from(method.getParameterNames());
			List<ClumpGroup> groups = new LinkedList<ClumpGroup>();
			for (ClumpSignature sig : sigs) {
				groups.add(getFromCache(sig));
			}
			return groups;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return new ArrayList<ClumpGroup>();
	}

	private ClumpGroup getFromCache(ClumpSignature sig) {
		Element element = dataClumpsCache.get(sig);
		if (element != null) {
			Object objectValue = element.getObjectValue();
			ClumpGroupHolder groupHolder = (ClumpGroupHolder) objectValue;
			System.out.println(" Found Clump Group \n" + groupHolder);
			ClumpGroup clumpGroup = groupHolder.getGroup(project);
			return clumpGroup;
		} else {
			System.out.println(" **** No Clump Found for Sig *** :" + sig);
			return new EmptyClumpGroup(sig);
		}
	}

	private EhcacheFactory getEhcacheFactory() {
		return EhcacheFactory.getInstance();
	}

	



}
