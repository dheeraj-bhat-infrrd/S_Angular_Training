# The script should be placed in back up folder
# Keep 2 back ups
if [[ -d "mongodb_2" ]]; then
	# delete the folder and rename mongodb_1 to mongodb_2 and mongodb to mongodb_1
	echo 'Removing last back up'
	sudo rm -r mongodb_2
	echo 'Moving first back up to second'
	sudo mv mongodb_1 mongodb_2
	echo 'Moving current back up to first'
	sudo mv mongodb mongodb_1
elif [[ -d "mongodb_1" ]]; then
	# rename mongodb_1 to mongodb_2 and mongodb to mongodb_1
	echo 'Moving first back up to second'
	sudo mv mongodb_1 mongodb_2
	echo 'Moving current back up to first'
	sudo mv mongodb mongodb_1
elif [[ -d "mongodb" ]]; then
	# rename mongodb to mongodb_1
	echo 'Moving current back up to first'
	sudo mv mongodb mongodb_1
fi
# backup mongo db
sudo cp -npRv /var/lib/mongodb/ ~/backup/
