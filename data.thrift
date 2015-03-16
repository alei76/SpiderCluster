namespace java cn.omartech.spider.gen

// thrift -gen python data.thrift
// thrift -gen java data.thrift

enum TaskType{
  Get =1,
  Post = 2,
}


struct Task{//具体抓取任务
  1: i64 id,
  2: string url,
  3: string headerJson,//json for map<string, string>
  4: string cookie,
  5: string refer,
  6: TaskType type = TaskType.Get,
  7: string localStoreFile,
  8: bool useProxy,
  9: string name,
  10: string parameterJson,//json for map<string, string>
  11: string parseRegex,//解析当前页中的链接，并返回该内容页
}

struct TaskResponse{//从服务端获取回的任务
  1: list<Task> tasks,
}

struct HtmlObject{
  1: string taskName,
  2: string url,
  3: string html,
  4: i64 taskId,
}


//service DataService{
//
//
//
//  ArticleResponse searchArticle(1: ArticleRequest req) //查询文章
//
//  ArticleResponse insertArticle(1: Article article) //保存文章
//
//  BeautyResponse searchBeauty(1: BeautyRequest req)
//
//  QieyexinxiResponse searchQiyexinxi(1: QiyexinxiRequest req)// 查询企业信息
//
//  JobResponse searchJobs(1: JobRequest req)//查询招聘
//  
//}
