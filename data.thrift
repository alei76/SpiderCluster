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
  8: bool useProxy = false,
  9: string name,
  10: string parameterJson,//json for map<string, string>
  11: string parseRegex,//解析当前页中的链接，并返回该内容页
  12: bool recursive = false,//如果是true，则继续抓取parseRegex中的链接
  13: string subTaskJson,//子任务
}
struct SubTask{//都不是必填
  1: string headerJson,
  2: string cookie,
  3: string url,
  4: i64 taskId,
  5: bool useProxy = false,

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
