#width = 720
#height= 1280

width = 120
height= 120

gride:
	./cartesian_plane_grid.sh
plane:
	./cartesian_plane.sh
tank:
	./tank.sh
new:
	./create_image.sh wall1 $(width) $(height) green
	./create_image.sh wall2 $(width) $(height) blue
	./create_image.sh wall3  $(width) $(height) red
	cp wall2_grid.png ../assets/gfx/wall2.png
	cp wall3_grid.png       ../assets/gfx/wall3.png
	cp wall1_grid.png       ../assets/gfx/wall1.png

clean:
	rm ../assets/gfx/wall2.png
	rm ../assets/gfx/wall3.png
	rm ../assets/gfx/wall1.png
	rm *.png
