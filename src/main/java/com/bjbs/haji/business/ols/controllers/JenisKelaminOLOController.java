package com.bjbs.haji.business.ols.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bjbs.haji.business.ols.JenisKelaminOLO;
import com.io.iona.implementations.pagination.DefaultPagingParameter;
import com.io.iona.springboot.actionflows.custom.CustomOnReadAllItems;
import com.io.iona.springboot.controllers.HibernateOptionListController;
import com.io.iona.springboot.sources.HibernateDataSource;
import com.io.iona.springboot.sources.HibernateDataUtility;

/**
 * Created By Aristo Pacitra
 * Example Option List Controller Hardcode
 */

@RestController
//@CrossOrigin(allowCredentials = "true")
@RequestMapping("/ol/jenis-kelamin")
public class JenisKelaminOLOController extends HibernateOptionListController<JenisKelaminOLO, JenisKelaminOLO>
implements CustomOnReadAllItems<JenisKelaminOLO,JenisKelaminOLO>{

	@Override
	public List<JenisKelaminOLO> onReadAllItems(HibernateDataUtility dataUtility,
			HibernateDataSource<JenisKelaminOLO, JenisKelaminOLO> dataSource, 
			DefaultPagingParameter pagingParameter) throws Exception {

		List<JenisKelaminOLO> result = new ArrayList<>();

		JenisKelaminOLO item1 = new JenisKelaminOLO(1, "Laki-Laki");
		JenisKelaminOLO item2 = new JenisKelaminOLO(2, "Perempuan");

		result.add(item1);
		result.add(item2);

		return result;
	}

}
