#디렉토리명이 프로젝트명
export project=${PWD##*/}
echo "project:${project}"

#토큰가져오기
export token=$(cat .git-token)
echo ${token}

git remote set-url origin https://ctc948040:${token}@github.com/ctc94/bybit-data.git

git add .

git status

echo "date:$(date "+%Y-%m-%d %_H:%M:%S")"

git commit -a -m "$(date "+%Y-%m-%d %_H:%M:%S") modify files"

git status

git push origin main
