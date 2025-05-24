package com.controller.admin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.core.AbstractRestController;
import com.core.Page;
import com.service.CmmService;
import com.util.CollaborativeFiltering;
import com.util.MapUtil;
import com.util.StringUtil;
import com.util.WebUtils;

@Controller
@SuppressWarnings("unchecked")
@RequestMapping("admin/shuju/*")
public class ShujuController extends AbstractRestController{	
	@Autowired
	CmmService service;
	
	private String tbNm = "shuju";
	
	/**
	 * 获取分页列表数据
	 * @param pMap
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getPageList.do")
	@ResponseBody
	public String getPageList(@RequestParam Map<String, Object> pMap, HttpServletRequest request){
		pMap.put("tbNm", this.tbNm);
		if (MapUtil.isContains(pMap, "name")) {
			pMap.put("where", " name like '%" + pMap.get("name") + "%'");
			pMap.put("name", "");
		}
		Page page = this.service.getDao().getPage(pMap);
		List<Map<String, Object>> dataList = page.getDataList();
		if (dataList!=null) {
			for(Map<String, Object> map : dataList) {
				map.put("cidNm", this.service.getCategoryNm(map.get("cid")));
				map.put("statusNm", this.service.getCodeNm("PRODUCT_STATUS", String.valueOf(map.get("status"))));
				map.put("tuijianNm", this.service.getCodeNm("YN", String.valueOf(map.get("tuijian"))));
				
			}
		}
		return WebUtils.responseLayuiJson(page);
	}
	@RequestMapping(value = "getPageList20.do")
	@ResponseBody
	public String getPageList20(@RequestParam Map<String, Object> pMap, HttpServletRequest request){
		pMap.put("tbNm", this.tbNm);
		if (MapUtil.isContains(pMap, "name")) {
			pMap.put("where", " name like '%" + pMap.get("name") + "%'");
			pMap.put("name", "");
		}
		pMap.put("iuid", this.service.getLoginUid(request));
		Page page = this.service.getDao().getPage(pMap);
		List<Map<String, Object>> dataList = page.getDataList();
		if (dataList!=null) {
			for(Map<String, Object> map : dataList) {
				map.put("cidNm", this.service.getCategoryNm(map.get("cid")));
				map.put("statusNm", this.service.getCodeNm("PRODUCT_STATUS", String.valueOf(map.get("status"))));
				map.put("tuijianNm", this.service.getCodeNm("YN", String.valueOf(map.get("tuijian"))));
				
			}
		}
		return WebUtils.responseLayuiJson(page);
	}
	/**
	 * 获取首页推荐列表
	 * @param pMap
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getTuijianListx.do")
	@ResponseBody
	public String getTuijianListx(@RequestParam Map<String, Object> pMap, HttpServletRequest request){
		HttpSession session = request.getSession();
		
			pMap.put("status", "10");
			pMap.put("tuijian", "Y");
			pMap.put("del_yn", "N");
			
			List<Map<String, Object>> rtList = service.getDao().getList(pMap, tbNm);
			if (rtList!=null) {
				for (Map<String, Object> map : rtList) {
				}
			}
			return JSON.toJSONString(rtList);
		
	}
	
	/**
	 * 获取首页点击列表
	 * @param pMap
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getApvList.do")
	@ResponseBody
	public String getApvList(@RequestParam Map<String, Object> pMap, HttpServletRequest request){
		pMap.put("status", "10");
		pMap.put("orderby", " pv desc");
		List<Map<String, Object>> rtList = service.getDao().getList(pMap, tbNm);
		if (rtList!=null) {
			for (Map<String, Object> map : rtList) {
				
			}
		}
		return JSON.toJSONString(rtList);
	}
	@RequestMapping(value = "getTuijianList.do")
	@ResponseBody
	public String getTuijianList(@RequestParam Map<String, Object> pMap, HttpServletRequest request){
		if (StringUtil.isEmpty(request.getSession().getAttribute("uid"))) {
			pMap.put("status", "10");
			pMap.put("tuijian", "Y");
			pMap.put("del_yn", "N");
			
			List<Map<String, Object>> rtList = service.getDao().getList(pMap, tbNm);
			if (rtList!=null) {
				for (Map<String, Object> map : rtList) {
					
				}
			}
			return JSON.toJSONString(rtList);
		}else {
			List<String> shujuIds = new ArrayList<String>();
			String userId = this.service.getLoginUid(request);//用户id
			int userId1 = Integer.parseInt(userId);
			String usersize = this.service.getDao().getOne("Select COUNT(*) AS num From suanfa ");//用户数量
			usersize = StringUtil.isEmpty(usersize)?"0":usersize;
			int usersize1 = Integer.parseInt(usersize);
			CollaborativeFiltering cf = new CollaborativeFiltering(); // 创建推荐系统实例
			Map<String, Object> tMapsf = new HashMap<>();
			List<Map<String, Object>> suanFa = service.getDao().getList(tMapsf, "suanfa");//调出用户信息
			System.out.print(suanFa);
		
		 		// 至少有一个列用于用户ID，但这里假设只有一个作为示例
				String[][] userRatingsData = {};
				ArrayList<ArrayList<String>> userRatingsDataList = new ArrayList();
				if (suanFa != null) {
				    for (int i = 0; i < suanFa.size(); i++) {
				    	Map<String, Object> tObjMap = suanFa.get(i);
				    	ArrayList<String> tUserData = new ArrayList();
				    	tUserData.add(String.valueOf(tObjMap.get("userid")));
				    	String str = "";
				    	for (Map.Entry<String, Object> entry : tObjMap.entrySet()) {
			    			String tKey = entry.getKey();
			    			if (tKey.startsWith("good") && (int)entry.getValue()>0){
			    				if (!"".equals(str)) {
			    					str += ",";
			    				}
			    				str += tKey.replace("good", "") + ":" + entry.getValue();
			    			}
			    		}
				    	tUserData.add(str);
				    	userRatingsDataList.add(tUserData);
				    }
				}

				// 循环添加用户评分
				for (ArrayList<String> row : userRatingsDataList) {
					 String userId11 = row.get(0);
					 String ratingsStr = row.get(1);
					 Map<String, Integer> userRatings = new HashMap<>();
		            String[] ratingPairs = ratingsStr.split(",");
		            for (String ratingPair : ratingPairs) {
		                String[] parts = ratingPair.split(":");
		                int rating = Integer.parseInt(parts[0]);
		                String itemId = parts[1];
		                
		                String sqla1 = "select * from "+tbNm+" where id='"+itemId+"' and status='10' and del_yn='N'";
		    			Map<String, Object> stocInfo = this.service.getDao().getInfo(sqla1);
		    			if (stocInfo.isEmpty()) {
		    				System.out.println("");
		    			} else {
		    				userRatings.put(itemId, rating);
		    			}
		            }
		            // 调用CollaborativeFiltering类的addUserRatings方法
		            CollaborativeFiltering.addUserRatings(userId11, userRatings);
		            System.out.println("加入结果userRatings: " + userRatings); // 打印推荐结果
				}


	        Map<String, Double> recommendations = cf.recommendItems(userId); // 为UserA推荐物品
	        System.out.println("推荐物品给 User1: " + recommendations); // 打印推荐结果
	        
	        List<Map<String, Object>> rtList = new ArrayList<Map<String, Object>>();
	        int count = 0;  // 计数器，用于限制结果数量
	        int num =Integer.valueOf(String.valueOf(pMap.get("limit")));
	        for (Map.Entry<String, Double> entry : recommendations.entrySet()) {
	            if (entry.getValue() <= 2.0) {
	                continue;  // 跳过值小于或等于2.0的条目
	            }
	            if (count >= num) {
	                break;  // 如果已经添加了条目，则停止循环
	            }
	            Map<String, Object> tMap = new HashMap<String, Object>();
	            tMap.put("id", entry.getKey());
	            tMap.put("value", entry.getValue());
	            tMap.put("icon", this.service.getDao().getFieldValById(entry.getKey(), "icon",tbNm));
	        	tMap.put("name", this.service.getDao().getFieldValById(entry.getKey(), "name",tbNm));
	        	tMap.put("descr", this.service.getDao().getFieldValById(entry.getKey(), "descr",tbNm));
	        	tMap.put("itime", this.service.getDao().getFieldValById(entry.getKey(), "itime",tbNm));
	        	tMap.put("pv", this.service.getDao().getFieldValById(entry.getKey(), "pv",tbNm));
	            rtList.add(tMap);
	            count++;  // 增加计数器
	        }
	        System.out.println(count);
	        if (count < num) {
	        	int nums = num-count;
				Map<String, Object> tMapa = new HashMap<String, Object>();
				tMapa.put("status", "10");
				tMapa.put("tuijian", "Y");
				tMapa.put("limit", nums);
				tMapa.put("del_yn", "N");
				List<Map<String, Object>> rtLista = service.getDao().getList(pMap, tbNm); // 确保 pMap 已正确设置以匹配需求
			    if (rtLista != null) {
			        // 这里不能直接添加 rtLista 到 rtList，因为 rtLista 是一个列表
			        // 应该逐个添加 rtLista 中的元素到 rtList
			        for (Map<String, Object> map : rtLista) {
			            if (rtList.size() < num) { // 确保不超过8个元素
			                rtList.add(map);
			            } else {
			                break; // 如果已经添加了8个，就停止添加
			            }
			        }
			    }
			}
			 
			return JSON.toJSONString(rtList);
		}
	}
	
	/**
	 * 获取首页列表
	 * @param pMap
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "getIndexPrdList.do")
	@ResponseBody
	public String getIndexPrdList(@RequestParam Map<String, Object> pMap, HttpServletRequest request){
		List<Map<String, Object>> rtList = new ArrayList<>();
		Map<String, Object> tMap = new HashMap<>();
		tMap.put("type", "PRODUCT");
		tMap.put("orderby", " sort asc");
		tMap.put("limit", "3");
		List<Map<String, Object>> cList = service.getDao().getList(tMap, "category");
		if (cList!=null){
			for (Map<String, Object> map : cList) {
				tMap = new HashMap<>();
				tMap.put("cid", map.get("id"));
				tMap.put("status", "10");
				tMap.put("limit", "5");
				List<Map<String, Object>> pList = service.getDao().getList(tMap, tbNm);
				if (pList!=null) {
					for (Map<String, Object> m : pList) {
						
					}
				}
				Map<String, Object> rtMap = new HashMap<>();
				rtMap.put("category", map);
				rtMap.put("product", pList);
				rtList.add(rtMap);
			}
		}
		if (rtList!=null) {
			for (Map<String, Object> map : rtList) {
			}
		}
		return JSON.toJSONString(rtList);
	}
	
	@RequestMapping(value = "getInfo.do")
	@ResponseBody
	public String getInfo(@RequestParam Map<String, Object> pMap, HttpServletRequest request){
		Map<String, Object> info = this.service.getDao().getInfoById(String.valueOf(pMap.get("id")), this.tbNm);
		info.put("cidNm", service.getCategoryNm(info.get("cid")));
		info.put("useridNm", service.getUsericon(info.get("userid")));
		String rtStr = JSON.toJSONString(info);
		return rtStr;
	}
	@RequestMapping(value = "addPv.do")
	@ResponseBody
	public String addPv(@RequestParam Map<String, Object> pMap, HttpServletRequest request){
		this.service.getDao().runSql("update "+tbNm+" set pv=pv+1 where id="+pMap.get("id"));
		//加分
		if (!StringUtil.isEmpty(request.getSession().getAttribute("uid"))) {
			service.getDao().runSql("update suanfa set good"+pMap.get("id")+"=good"+pMap.get("id")+"+1 where userid="+request.getSession().getAttribute("uid"));
		}
		return null;
	}
	
	
	@RequestMapping(value = "update.do")
	@ResponseBody
	public String update(@RequestParam Map<String, Object> pMap, HttpServletRequest request){
		Map<String, Object> tObjMap = new HashMap<>();
		tObjMap.put("no", pMap.get("no"));
		List<Map<String, Object>> haveList = this.service.getDao().getList(tObjMap, tbNm);
		if (!MapUtil.isContains(pMap, "id")) {
			pMap.put("iuid", this.service.getLoginUid(request));
			String id = this.service.getDao().add(pMap, this.tbNm);
			service.getDao().runSql("ALTER TABLE suanfa ADD good"+id+" INT( 11 ) NULL DEFAULT '1'");   
		} else {
			
			this.service.getDao().update(pMap, this.tbNm);
		}
		return WebUtils.successResp(null,"操作成功");
	}
	
	@RequestMapping(value = "updateBatch.do")
	@ResponseBody
	public String updateBatch(@RequestParam Map<String, Object> pMap, HttpServletRequest request){
		if (!MapUtil.isContains(pMap, "id")) {
			return WebUtils.errorResp("ID不能为空！");
		}
		if (MapUtil.isContains(pMap, "del_yn") && "Y".equals(pMap.get("del_yn"))) {
			service.getDao().runSql("ALTER TABLE suanfa DROP good"+pMap.get("id")); 
		}
		this.service.getDao().update(pMap, this.tbNm);
		return WebUtils.successResp(null,"操作成功");
	}
	
	/**
	 * 删除
	 * @param pMap
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "del")
	@ResponseBody
	public String del(@RequestParam Map<String, Object> pMap, HttpServletRequest request){
		service.getDao().runSql("ALTER TABLE suanfa DROP good"+pMap.get("id")); 
		Map<String, Object> tObjMap = new HashMap<>();
		tObjMap.put("id", pMap.get("id"));
		tObjMap.put("del_yn", "Y");
		service.getDao().update(tObjMap, tbNm);
		return WebUtils.successResp(null,"操作成功");
	}
	
	
	
	

}
