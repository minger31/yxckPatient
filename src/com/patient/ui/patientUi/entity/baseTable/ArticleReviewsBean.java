package com.patient.ui.patientUi.entity.baseTable;

import java.io.Serializable;

public class ArticleReviewsBean implements Serializable {

	/**
	 * 文章评论
	 */
	private static final long serialVersionUID = -3730309221094579333L;
	public String artiRevId;// ID
	public String commentsId;// 评论人ID
	public String articleId;// 文章ID
	public String commentsName;// 评论人姓名
	public String commentsTime;// 评论时间
	public String content;// 内容
}
