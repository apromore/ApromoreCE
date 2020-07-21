package nl.rug.ds.bpm.variability;

import nl.rug.ds.bpm.exception.SpecificationException;
import nl.rug.ds.bpm.specification.jaxb.BPMSpecification;
import nl.rug.ds.bpm.specification.jaxb.Group;
import nl.rug.ds.bpm.specification.jaxb.Specification;
import nl.rug.ds.bpm.specification.jaxb.SpecificationSet;
import nl.rug.ds.bpm.specification.marshaller.SpecificationMarshaller;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class SpecificationToXML {

	public static String[] getOutput(VariabilitySpecification vs, String silentprefix) {
		return getOutput(vs, silentprefix, true, true, true, true, true, true);
	}

	public static String[] getOutput(VariabilitySpecification vs, String silentprefix,
									 Boolean viresp, Boolean viprec, Boolean veiresp, Boolean veresp, Boolean vconf, Boolean vpar) {

		String output[] = new String[2];
		String plaintext = "";

		SpecificationTypeLoader stl = new SpecificationTypeLoader();

		BPMSpecification bpmspec = new BPMSpecification();
		SpecificationSet specset;
		Specification spec;
		Group grp;
		String spectype;

		int id = 1;

		if (viresp) {
			specset = new SpecificationSet();
			spectype = "AlwaysImmediateResponse";
			for (String sp: vs.getViresp()) {
				grp = SpecificationBuilder.getGroup(sp, "U", "");

				if (grp.getElements().size() > 1) {
					spec = SpecificationBuilder.getSpecification(sp, "s" + id, spectype, grp.getId());
					bpmspec.addGroup(grp);
				}
				else {
					spec = SpecificationBuilder.getSpecification(sp, "s" + id, spectype);
				}

				specset.addSpecification(spec);

				id++;

				plaintext += sp + "\n";
			}
			bpmspec.addSpecificationType(stl.getSpecificationType(spectype));
			bpmspec.addSpecificationSet(specset);

			plaintext += "\n";
		}

		if (viprec) {
			specset = new SpecificationSet();
			spectype = "AlwaysImmediatePrecedence";
			for (String sp: vs.getViprec()) {
				grp = SpecificationBuilder.getGroup(sp, "", "U");

				if (grp.getElements().size() > 1) {
					spec = SpecificationBuilder.getSpecification(sp, "s" + id, spectype, grp.getId());
					bpmspec.addGroup(grp);
				}
				else {
					spec = SpecificationBuilder.getSpecification(sp, "s" + id, spectype);
				}

				specset.addSpecification(spec);
				id++;

				plaintext += sp + "\n";
			}
			bpmspec.addSpecificationType(stl.getSpecificationType(spectype));
			bpmspec.addSpecificationSet(specset);

			plaintext += "\n";
		}

		if (veiresp) {
			specset = new SpecificationSet();
			spectype = "ExistImmediateResponse";
			for (String sp: vs.getVeiresp()) {
				spec = SpecificationBuilder.getSpecification(sp, "s" + id, spectype);
				specset.addSpecification(spec);
				id++;

				plaintext += sp + "\n";
			}
			bpmspec.addSpecificationType(stl.getSpecificationType(spectype));
			bpmspec.addSpecificationSet(specset);

			plaintext += "\n";
		}

		if (veresp) {
			specset = new SpecificationSet();
			spectype = "ExistResponse";
			for (String sp: vs.getVerespReduced(false)) {
				spec = SpecificationBuilder.getSpecification(sp, "s" + id, spectype);
				specset.addSpecification(spec);
				id++;

				plaintext += sp + "\n";
			}
			bpmspec.addSpecificationType(stl.getSpecificationType(spectype));
			bpmspec.addSpecificationSet(specset);

			plaintext += "\n";
		}

		if (vconf) {
			specset = new SpecificationSet();
			spectype = "AlwaysConflict";
			for (String sp: vs.getVconf()) {
				spec = SpecificationBuilder.getSpecification(sp, "s" + id, spectype);
				specset.addSpecification(spec);
				id++;

				plaintext += sp + "\n";
			}
			bpmspec.addSpecificationType(stl.getSpecificationType(spectype));
			bpmspec.addSpecificationSet(specset);

			plaintext += "\n";
		}

		if (vpar) {
			specset = new SpecificationSet();
			spectype = "AlwaysParallel";
			for (String sp: vs.getVpar()) {
				grp = SpecificationBuilder.getGroup(sp);

				spec = SpecificationBuilder.getSpecification(sp, "s" + id, spectype, grp.getId());
				specset.addSpecification(spec);
				bpmspec.addGroup(grp);
				id++;

				plaintext += sp + "\n";
			}
			bpmspec.addSpecificationType(stl.getSpecificationType(spectype));
			bpmspec.addSpecificationSet(specset);

		}

		OutputStream os = new ByteArrayOutputStream();
		try {
			new SpecificationMarshaller(bpmspec, os);
		} catch (SpecificationException e) {
			e.printStackTrace();
		}

		output[0] = os.toString();
		output[1] = plaintext;

		return output;
	}

}
